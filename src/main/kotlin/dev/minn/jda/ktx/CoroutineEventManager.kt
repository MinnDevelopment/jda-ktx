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

package dev.minn.jda.ktx

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.hooks.EventListener
import net.dv8tion.jda.api.hooks.IEventManager
import org.slf4j.Logger
import java.util.concurrent.CopyOnWriteArrayList

/**
 * EventManager implementation which supports both [EventListener] and [CoroutineEventListener].
 *
 * This enables [the coroutine listener extension][listener].
 */
class CoroutineEventManager(
    private val scope: CoroutineScope = GlobalScope,
    /** Timeout in milliseconds each event listener is allowed to run. Set to -1 for no timeout. Default: -1 */
    var timeout: Long = -1
) : IEventManager {
    private val log: Logger by SLF4J
    private val listeners = CopyOnWriteArrayList<Any>()

    override fun handle(event: GenericEvent) {
        scope.launch {
            for (listener in listeners) {
                if (timeout > 0) {
                    // Timeout only works when the continuations implement a cancellation handler
                    val result = withTimeoutOrNull(timeout) {
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

    private suspend fun runListener(listener: Any?, event: GenericEvent) {
        when (listener) {
            is CoroutineEventListener -> listener.onEvent(event)
            is EventListener -> listener.onEvent(event)
        }
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
}
