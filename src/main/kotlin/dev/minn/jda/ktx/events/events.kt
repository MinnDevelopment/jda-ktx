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

import kotlinx.coroutines.suspendCancellableCoroutine
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent
import net.dv8tion.jda.api.events.interaction.command.GenericContextInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.EntitySelectInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent
import net.dv8tion.jda.api.hooks.EventListener
import net.dv8tion.jda.api.hooks.SubscribeEvent
import net.dv8tion.jda.api.interactions.commands.context.ContextInteraction
import net.dv8tion.jda.api.interactions.commands.context.UserContextInteraction
import net.dv8tion.jda.api.sharding.ShardManager
import kotlin.coroutines.resume
import kotlin.time.Duration

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
 * @param[timeout] The timeout [Duration] to use for this listener, or null to use the default from the event manager
 * @param[consumer] The event consumer function
 *
 * @return[CoroutineEventListener] The created event listener instance (can be used to remove later)
 */
inline fun <reified T : GenericEvent> JDA.listener(timeout: Duration? = null, crossinline consumer: suspend CoroutineEventListener.(T) -> Unit): CoroutineEventListener {
    return (eventManager as CoroutineEventManager).listener(timeout, consumer)
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
 * @param[timeout] The timeout [Duration] to use for this listener, or null to use the default from the event manager
 * @param[consumer] The event consumer function
 *
 * @return[CoroutineEventListener] The created event listener instance (can be used to remove later)
 */
inline fun <reified T : GenericEvent> ShardManager.listener(timeout: Duration? = null, crossinline consumer: suspend CoroutineEventListener.(T) -> Unit): CoroutineEventListener {
    return object : CoroutineEventListener {
        override val timeout: EventTimeout
            get() = timeout.toTimeout()

        override fun cancel() {
            return removeEventListener(this)
        }

        override suspend fun onEvent(event: GenericEvent) {
            if (event is T)
                consumer(event)
        }
    }.also { addEventListener(it) }
}


/**
 * Requires [CoroutineEventManager] to be used!
 *
 * Opens an event listener scope for simple hooking. This is a special listener which is used to listen for commands!
 *
 * ## Example
 *
 * ```kotlin
 * jda.onCommand("ping") { event ->
 *     event.reply("Pong!").queue()
 * }
 * ```
 *
 * @param[name] The command name
 * @param[timeout] The timeout [Duration] to use for this listener, or null to use the default from the event manager
 * @param[consumer] The event consumer function
 *
 * @return[CoroutineEventListener] The created event listener instance (can be used to remove later)
 */
fun JDA.onCommand(name: String, timeout: Duration? = null, consumer: suspend CoroutineEventListener.(GenericCommandInteractionEvent) -> Unit) = listener<GenericCommandInteractionEvent>(timeout=timeout) {
    if (it.name == name)
        consumer(it)
}

/**
 * Requires [CoroutineEventManager] to be used!
 *
 * Opens an event listener scope for simple hooking. This is a special listener which is used to listen for commands!
 *
 * ## Example
 *
 * ```kotlin
 * shardManager.onCommand("ping") { event ->
 *     event.reply("Pong!").queue()
 * }
 * ```
 *
 * @param[name] The command name
 * @param[timeout] The timeout [Duration] to use for this listener, or null to use the default from the event manager
 * @param[consumer] The event consumer function
 *
 * @return[CoroutineEventListener] The created event listener instance (can be used to remove later)
 */
fun ShardManager.onCommand(name: String, timeout: Duration? = null, consumer: suspend CoroutineEventListener.(GenericCommandInteractionEvent) -> Unit) = listener<GenericCommandInteractionEvent>(timeout=timeout) {
    if (it.name == name)
        consumer(it)
}



/**
 * Requires [CoroutineEventManager] to be used!
 *
 * Opens an event listener scope for simple hooking. This is a special listener which is used to listen for context menu commands!
 *
 * ## Example
 *
 * ```kotlin
 * jda.onContext<Message>("Pin Message") { event ->
 *     event.target.pin().await()
 *     event.reply("${event.user.asMention} pinned a message in this channel!").queue()
 * }
 * ```
 *
 * @param[name] The command name
 * @param[timeout] The timeout [Duration] to use for this listener, or null to use the default from the event manager
 * @param[consumer] The event consumer function
 *
 * @return[CoroutineEventListener] The created event listener instance (can be used to remove later)
 */
@Suppress("UNCHECKED_CAST")
inline fun <reified T> JDA.onContext(
    name: String,
    timeout: Duration? = null,
    crossinline consumer: suspend CoroutineEventListener.(GenericContextInteractionEvent<T>) -> Unit
) = (this.eventManager as CoroutineEventManager).listener<GenericCommandInteractionEvent>(timeout=timeout) {
    if (it.name == name && it is GenericContextInteractionEvent<*> && it.target is T)
        consumer(it as GenericContextInteractionEvent<T>)
}

/**
 * Requires [CoroutineEventManager] to be used!
 *
 * Opens an event listener scope for simple hooking. This is a special listener which is used to listen for context menu commands!
 *
 * ## Example
 *
 * ```kotlin
 * shardManager.onContext<Message>("Pin Message") { event ->
 *     event.target.pin().await()
 *     event.reply("${event.user.asMention} pinned a message in this channel!").queue()
 * }
 * ```
 *
 * @param[name] The command name
 * @param[timeout] The timeout [Duration] to use for this listener, or null to use the default from the event manager
 * @param[consumer] The event consumer function
 *
 * @return[CoroutineEventListener] The created event listener instance (can be used to remove later)
 */
@Suppress("UNCHECKED_CAST")
inline fun <reified T> ShardManager.onContext(
    name: String,
    timeout: Duration? = null,
    crossinline consumer: suspend CoroutineEventListener.(GenericContextInteractionEvent<T>) -> Unit
) = listener<GenericCommandInteractionEvent>(timeout=timeout) {
    if (it.name == name && it is GenericContextInteractionEvent<*> && it.target is T)
        consumer(it as GenericContextInteractionEvent<T>)
}



/**
 * Requires [CoroutineEventManager] to be used!
 *
 * Opens an event listener scope for simple hooking. This is a special listener which is used to listen for command autocomplete interactions!
 *
 * ## Example
 *
 * ```kotlin
 * jda.onCommandAutocomplete("play", "track") { event ->
 *     event.replyChoiceStrings(youtube.search(event.focusedOption.value)).queue()
 * }
 * ```
 *
 * @param[name] The command name
 * @param[option] The option name (or null to run for all options)
 * @param[timeout] The timeout [Duration] to use for this listener, or null to use the default from the event manager
 * @param[consumer] The event consumer function
 *
 * @return[CoroutineEventListener] The created event listener instance (can be used to remove later)
 */
fun JDA.onCommandAutocomplete(name: String, option: String? = null, timeout: Duration? = null, consumer: suspend CoroutineEventListener.(CommandAutoCompleteInteractionEvent) -> Unit) = listener<CommandAutoCompleteInteractionEvent>(timeout=timeout) {
    if (it.name == name && (option == null || it.focusedOption.name == option))
        consumer(it)
}

/**
 * Requires [CoroutineEventManager] to be used!
 *
 * Opens an event listener scope for simple hooking. This is a special listener which is used to listen for command autocomplete interactions!
 *
 * ## Example
 *
 * ```kotlin
 * shardManager.onCommandAutocomplete("play", "track") { event ->
 *     event.replyChoiceStrings(youtube.search(event.focusedOption.value)).queue()
 * }
 * ```
 *
 * @param[name] The command name
 * @param[option] The option name (or null to run for all options)
 * @param[timeout] The timeout [Duration] to use for this listener, or null to use the default from the event manager
 * @param[consumer] The event consumer function
 *
 * @return[CoroutineEventListener] The created event listener instance (can be used to remove later)
 */
fun ShardManager.onCommandAutocomplete(name: String, option: String? = null, timeout: Duration? = null, consumer: suspend CoroutineEventListener.(CommandAutoCompleteInteractionEvent) -> Unit) = listener<CommandAutoCompleteInteractionEvent>(timeout=timeout) {
    if (it.name == name && (option == null || it.focusedOption.name == option))
        consumer(it)
}



/**
 * Requires [CoroutineEventManager] to be used!
 *
 * Opens an event listener scope for simple hooking. This is a special listener which is used to listen for button presses!
 *
 * ## Example
 *
 * ```kotlin
 * jda.on<ButtonClickEvent>("delete") { event ->
 *     event.deferEdit().queue()
 *     event.hook.deleteOriginal().queue()
 * }
 * ```
 *
 * @param[customId] The button id
 * @param[timeout] The timeout [Duration] to use for this listener, or null to use the default from the event manager
 * @param[consumer] The event consumer function
 *
 * @return[CoroutineEventListener] The created event listener instance (can be used to remove later)
 */
inline fun <reified T : GenericComponentInteractionCreateEvent> JDA.onComponent(customId: String, timeout: Duration? = null, crossinline consumer: suspend CoroutineEventListener.(T) -> Unit) = listener<T>(timeout=timeout) {
    if (it.componentId == customId)
        consumer(it)
}

/**
 * Requires [CoroutineEventManager] to be used!
 *
 * Opens an event listener scope for simple hooking. This is a special listener which is used to listen for button presses!
 *
 * ## Example
 *
 * ```kotlin
 * shardManager.on<ButtonClickEvent>("delete") { event ->
 *     event.deferEdit().queue()
 *     event.hook.deleteOriginal().queue()
 * }
 * ```
 *
 * @param[customId] The button id
 * @param[timeout] The timeout [Duration] to use for this listener, or null to use the default from the event manager
 * @param[consumer] The event consumer function
 *
 * @return[CoroutineEventListener] The created event listener instance (can be used to remove later)
 */
inline fun <reified T : GenericComponentInteractionCreateEvent> ShardManager.onComponent(customId: String, timeout: Duration? = null, crossinline consumer: suspend CoroutineEventListener.(T) -> Unit) = listener<T>(timeout=timeout) {
    if (it.componentId == customId)
        consumer(it)
}



/**
 * Requires [CoroutineEventManager] to be used!
 *
 * Opens an event listener scope for simple hooking. This is a special listener which is used to listen for button presses!
 *
 * ## Example
 *
 * ```kotlin
 * jda.onButton("delete") { event ->
 *     event.deferEdit().queue()
 *     event.hook.deleteOriginal().queue()
 * }
 * ```
 *
 * @param[customId] The button id
 * @param[timeout] The timeout [Duration] to use for this listener, or null to use the default from the event manager
 * @param[consumer] The event consumer function
 *
 * @return[CoroutineEventListener] The created event listener instance (can be used to remove later)
 */
fun JDA.onButton(customId: String, timeout: Duration? = null, consumer: suspend CoroutineEventListener.(ButtonInteractionEvent) -> Unit) = onComponent(customId, timeout, consumer)

/**
 * Requires [CoroutineEventManager] to be used!
 *
 * Opens an event listener scope for simple hooking. This is a special listener which is used to listen for button presses!
 *
 * ## Example
 *
 * ```kotlin
 * shardManager.onButton("delete") { event ->
 *     event.deferEdit().queue()
 *     event.hook.deleteOriginal().queue()
 * }
 * ```
 *
 * @param[customId] The button id
 * @param[timeout] The timeout [Duration] to use for this listener, or null to use the default from the event manager
 * @param[consumer] The event consumer function
 *
 * @return[CoroutineEventListener] The created event listener instance (can be used to remove later)
 */
fun ShardManager.onButton(customId: String, timeout: Duration? = null, consumer: suspend CoroutineEventListener.(ButtonInteractionEvent) -> Unit) = onComponent(customId, timeout, consumer)



/**
 * Requires [CoroutineEventManager] to be used!
 *
 * Opens an event listener scope for simple hooking. This is a special listener which is used to listen for selection menu events!
 *
 * ## Example
 *
 * ```kotlin
 * jda.onStringSelect("menu:class") { event ->
 *     event.deferEdit().queue()
 *     println("User selected: ${event.values}")
 * }
 * ```
 *
 * @param[customId] The selection menu id
 * @param[timeout] The timeout [Duration] to use for this listener, or null to use the default from the event manager
 * @param[consumer] The event consumer function
 *
 * @return[CoroutineEventListener] The created event listener instance (can be used to remove later)
 */
fun JDA.onStringSelect(customId: String, timeout: Duration? = null, consumer: suspend CoroutineEventListener.(StringSelectInteractionEvent) -> Unit) = onComponent(customId, timeout, consumer)

/**
 * Requires [CoroutineEventManager] to be used!
 *
 * Opens an event listener scope for simple hooking. This is a special listener which is used to listen for selection menu events!
 *
 * ## Example
 *
 * ```kotlin
 * shardManager.onStringSelect("menu:class") { event ->
 *     event.deferEdit().queue()
 *     println("User selected: ${event.values}")
 * }
 * ```
 *
 * @param[customId] The selection menu id
 * @param[timeout] The timeout [Duration] to use for this listener, or null to use the default from the event manager
 * @param[consumer] The event consumer function
 *
 * @return[CoroutineEventListener] The created event listener instance (can be used to remove later)
 */
fun ShardManager.onStringSelect(customId: String, timeout: Duration? = null, consumer: suspend CoroutineEventListener.(StringSelectInteractionEvent) -> Unit) = onComponent(customId, timeout, consumer)

/**
 * Requires [CoroutineEventManager] to be used!
 *
 * Opens an event listener scope for simple hooking. This is a special listener which is used to listen for selection menu events!
 *
 * ## Example
 *
 * ```kotlin
 * jda.onEntitySelect("elevate:menu") { event ->
 *     event.deferEdit().queue()
 *     println("User selected users: ${event.mentions.users}")
 * }
 * ```
 *
 * @param[customId] The selection menu id
 * @param[timeout] The timeout [Duration] to use for this listener, or null to use the default from the event manager
 * @param[consumer] The event consumer function
 *
 * @return[CoroutineEventListener] The created event listener instance (can be used to remove later)
 */
fun JDA.onEntitySelect(customId: String, timeout: Duration? = null, consumer: suspend CoroutineEventListener.(EntitySelectInteractionEvent) -> Unit) = onComponent(customId, timeout, consumer)

/**
 * Requires [CoroutineEventManager] to be used!
 *
 * Opens an event listener scope for simple hooking. This is a special listener which is used to listen for selection menu events!
 *
 * ## Example
 *
 * ```kotlin
 * jda.onEntitySelect("elevate:menu") { event ->
 *     event.deferEdit().queue()
 *     println("User selected users: ${event.mentions.users}")
 * }
 * ```
 *
 * @param[customId] The selection menu id
 * @param[timeout] The timeout [Duration] to use for this listener, or null to use the default from the event manager
 * @param[consumer] The event consumer function
 *
 * @return[CoroutineEventListener] The created event listener instance (can be used to remove later)
 */
fun ShardManager.onEntitySelect(customId: String, timeout: Duration? = null, consumer: suspend CoroutineEventListener.(EntitySelectInteractionEvent) -> Unit) = onComponent(customId, timeout, consumer)



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

/**
 * If this context menu command was used in a [Guild][net.dv8tion.jda.api.entities.Guild],
 * this returns the member instance for the target user.
 *
 * @return The target member instance, or null if this was not in a guild.
 *
 * @see UserContextInteraction.getTargetMember
 */
val ContextInteraction<User>.targetMember: Member? get() = (this as UserContextInteraction).targetMember
