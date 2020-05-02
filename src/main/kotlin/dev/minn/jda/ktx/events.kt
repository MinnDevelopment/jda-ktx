package dev.minn.jda.ktx

import kotlinx.coroutines.suspendCancellableCoroutine
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.hooks.EventListener
import net.dv8tion.jda.api.hooks.SubscribeEvent
import net.dv8tion.jda.api.sharding.ShardManager
import kotlin.coroutines.resume

/**
 * Requires [CoroutineEventManager] to be used!
 *
 * Opens an event listener scope for simple hooking.
 *
 * ## Example
 *
 * ```kotlin
 * jda.listener<MessageReceivedEvent> { event ->
 *     println(event.message.contentRaw)
 * }
 * ```
 *
 * @param[consumer] The event consumer function
 *
 * @return[CoroutineEventListener] The created event listener instance (can be used to remove later)
 */
inline fun <reified T : GenericEvent> JDA.listener(crossinline consumer: suspend (T) -> Unit): CoroutineEventListener {
    return object : CoroutineEventListener {
        override suspend fun onEvent(event: GenericEvent) {
            if (event is T)
                consumer(event)
        }
    }.also { addEventListener(it) }
}

/**
 * Requires [CoroutineEventManager] to be used!
 *
 * Opens an event listener scope for simple hooking.
 *
 * ## Example
 *
 * ```kotlin
 * shardManager.listener<MessageReceivedEvent> { event ->
 *     println(event.message.contentRaw)
 * }
 * ```
 *
 * @param[consumer] The event consumer function
 *
 * @return[CoroutineEventListener] The created event listener instance (can be used to remove later)
 */
inline fun <reified T : GenericEvent> ShardManager.listener(crossinline consumer: suspend (T) -> Unit): CoroutineEventListener {
    return object : CoroutineEventListener {
        override suspend fun onEvent(event: GenericEvent) {
            if (event is T)
                consumer(event)
        }
    }.also { addEventListener(it) }
}

/**
 * Requires an EventManager implementation that supports either [EventListener] or [SubscribeEvent].
 *
 * Awaits a single event and then returns it. You can use the filter function to skip unwanted events for a simpler
 * code structure.
 *
 * ## Example
 *
 * ```kotlin
 * fun onMessage(message: Message) {
 *   if (message.contentRaw == "Hello Bot") {
 *     // Send confirmation message
 *     message.channel.sendTyping().await()
 *     message.channel.sendMessage("Hello, how are you?").queue()
 *     // Wait for user's response
 *     val nextEvent = message.jda.await<MessageReceivedEvent> { it.author == message.author }
 *     println("User responded with ${nextEvent.message.contentDisplay}")
 *   }
 * }
 * ```
 *
 * @param[filter] The event filter function (optional)
 *
 * @return The filtered event
 */
suspend inline fun <reified T : GenericEvent> JDA.await(crossinline filter: (T) -> Boolean = { true }) = suspendCancellableCoroutine<T> {
    val listener = object : EventListener {
        @SubscribeEvent
        override fun onEvent(event: GenericEvent) {
            if (event is T && filter(event)) {
                removeEventListener(this)
                it.resume(event)
            }
        }
    }
    addEventListener(listener)
    it.invokeOnCancellation { removeEventListener(listener) }
}

/**
 * Requires an EventManager implementation that supports either [EventListener] or [SubscribeEvent].
 *
 * Awaits a single event and then returns it. You can use the filter function to skip unwanted events for a simpler
 * code structure.
 *
 * ## Example
 *
 * ```kotlin
 * fun onMessage(message: Message) {
 *   if (message.contentRaw == "Hello Bot") {
 *     // Send confirmation message
 *     message.channel.sendTyping().await()
 *     message.channel.sendMessage("Hello, how are you?").queue()
 *     // Wait for user's response
 *     val nextEvent = message.jda.await<MessageReceivedEvent> { it.author == message.author }
 *     println("User responded with ${nextEvent.message.contentDisplay}")
 *   }
 * }
 * ```
 *
 * @param[filter] The event filter function (optional)
 *
 * @return The filtered event
 */
suspend inline fun <reified T : GenericEvent> ShardManager.await(crossinline filter: (T) -> Boolean = { true }) = suspendCancellableCoroutine<T> {
    val listener = object : EventListener {
        @SubscribeEvent
        override fun onEvent(event: GenericEvent) {
            if (event is T && filter(event)) {
                removeEventListener(this)
                it.resume(event)
            }
        }
    }
    addEventListener(listener)
    it.invokeOnCancellation { removeEventListener(listener) }
}