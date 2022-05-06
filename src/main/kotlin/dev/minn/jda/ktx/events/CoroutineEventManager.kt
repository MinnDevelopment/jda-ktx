/*
 * Copyright 2020 Florian Spieß
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.minn.jda.ktx.events

import dev.minn.jda.ktx.util.SLF4J
import kotlinx.coroutines.*
import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.hooks.EventListener
import net.dv8tion.jda.api.hooks.IEventManager
import org.slf4j.Logger
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.Executor
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.time.Duration

private val log: Logger by SLF4J<CoroutineEventManager>()

/**
 * Creates a suitable coroutine scope for your needs.
 *
 * @param[pool] The executor used to dispatch coroutines, uses [Dispatchers.Default] if not provided
 * @param[job] The parent job used for coroutines which can be used to cancel all children, uses [SupervisorJob] by default
 * @param[errorHandler] The [CoroutineExceptionHandler] used for handling uncaught exceptions, uses a logging handler which cancels the parent job on [Error] by default
 * @param[context] Any additional context to add to the scope, uses [EmptyCoroutineContext] by default
 *
 * @return[CoroutineScope]
 */
fun getDefaultScope(
    pool: Executor? = null,
    job: Job? = null,
    errorHandler: CoroutineExceptionHandler? = null,
    context: CoroutineContext = EmptyCoroutineContext
): CoroutineScope {
    val dispatcher = pool?.asCoroutineDispatcher() ?: Dispatchers.Default
    val parent = job ?: SupervisorJob()
    val handler = errorHandler ?: CoroutineExceptionHandler { _, throwable ->
        log.error("Uncaught exception from coroutine", throwable)
        if (throwable is Error) {
            parent.cancel()
            throw throwable
        }
    }
    return CoroutineScope(dispatcher + parent + handler + context)
}

/**
 * EventManager implementation which supports both [EventListener] and [CoroutineEventListener].
 *
 * This enables [the coroutine listener extension][listener].
 */
open class CoroutineEventManager(
    scope: CoroutineScope = getDefaultScope(),
    /** Timeout [Duration] each event listener is allowed to run. Set to [Duration.INFINITE] for no timeout. Default: [Duration.INFINITE] */
    var timeout: Duration = Duration.INFINITE
) : IEventManager, CoroutineScope by scope {
    protected val listeners = CopyOnWriteArrayList<Any>()

    protected fun timeout(listener: Any) = when {
        listener is CoroutineEventListener && listener.timeout != EventTimeout.Inherit -> listener.timeout.time
        else -> timeout
    }

    override fun handle(event: GenericEvent) {
        launch {
            for (listener in listeners) {
                val actualTimeout = timeout(listener)
                if (actualTimeout.isPositive() && actualTimeout.isFinite()) {
                    // Timeout only works when the continuations implement a cancellation handler
                    val result = withTimeoutOrNull(actualTimeout.inWholeMilliseconds) {
                        runListener(listener, event)
                    }
                    if (result == null) {
                        log.debug("Event of type ${event.javaClass.simpleName} timed out.")
                    }
                } else {
                    runListener(listener, event)
                }
            }
        }
    }

    protected open suspend fun runListener(listener: Any, event: GenericEvent) = try {
        when (listener) {
            is CoroutineEventListener -> listener.onEvent(event)
            is EventListener -> listener.onEvent(event)
            else -> Unit
        }
    } catch (ex: Exception) {
        log.error("Uncaught exception in event listener", ex)
    }

    override fun register(listener: Any) {
        listeners.add(when (listener) {
            is EventListener, is CoroutineEventListener -> listener
            else -> throw IllegalArgumentException("Listener must implement either EventListener or CoroutineEventListener")
        })
    }

    override fun getRegisteredListeners(): MutableList<Any> = mutableListOf(listeners)

    override fun unregister(listener: Any) {
        listeners.remove(listener)
    }

    /**
     * Opens an event listener scope for simple hooking.
     *
     * ## Example
     *
     * ```kotlin
     * manager.listener<MessageReceivedEvent> { event ->
     *     println(event.message.contentRaw)
     * }
     * ```
     *
     * @param[timeout] The timeout [Duration] to use for this listener, or null to use the default from the event manager
     * @param[consumer] The event consumer function
     *
     * @return[CoroutineEventListener] The created event listener instance (can be used to remove later)
     */
    inline fun <reified T : GenericEvent> listener(timeout: Duration? = null, crossinline consumer: suspend CoroutineEventListener.(T) -> Unit): CoroutineEventListener {
        return object : CoroutineEventListener {
            override val timeout: EventTimeout
                get() = timeout.toTimeout()

            override fun cancel() {
                return unregister(this)
            }

            override suspend fun onEvent(event: GenericEvent) {
                if (event is T)
                    consumer(event)
            }
        }.also { register(it) }
    }
}
