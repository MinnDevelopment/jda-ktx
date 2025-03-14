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

import net.dv8tion.jda.api.components.button.Button
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.EventListener
import net.dv8tion.jda.api.hooks.SubscribeEvent

/**
 * Requires an EventManager implementation that supports either [EventListener] or [SubscribeEvent].
 *
 * Waits for a message in the target channel.
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
 *     val nextMessage = message.channel.awaitMessage(message.author)
 *     println("User responded with ${nextMessage.contentDisplay}")
 *   }
 * }
 * ```
 *
 * @param[author] A specific user to filter for (optional)
 * @param[filter] A filter function to simplify code flow (optional)
 *
 * @return[Message]
 */
suspend inline fun MessageChannel.awaitMessage(
    author: User? = null, // filter by user
    crossinline filter: (Message) -> Boolean = { true } // filter by filter (lol)
): Message {
    return jda.await<MessageReceivedEvent> {
        it.channel == this
            && (author == null || it.author == author)
            && filter(it.message)
    }.message
}

/**
 * Requires an EventManager implementation that supports either [EventListener] or [SubscribeEvent].
 *
 * Waits for button to be pressed/clicked in the target channel.
 *
 * ## Example
 *
 * ```kotlin
 * jda.onCommand("ban") { event ->
 *   val user = event.getOption("user")!!.asUser
 *   event.reply("Are you sure you want to ban ${user.asMention}?")
 *     .addActionRow(Button.danger("${user.id}:confirm", "Confirm"))
 *     .setEphemeral(true)
 *     .queue()
 *   val pressed = event.channel.awaitButton("${user.id}:confirm")
 *   pressed.deferEdit().queue() // don't forget to acknowledge
 *   event.guild.ban(user, 0).queue()
 * }
 * ```
 *
 * @param[id] The button id
 * @param[user] A specific user to filter for (optional)
 * @param[filter] A filter function to simplify code flow (optional)
 *
 * @return[ButtonInteractionEvent]
 */
suspend inline fun MessageChannel.awaitButton(id: String, user: User? = null, crossinline filter: (ButtonInteractionEvent) -> Boolean = { true }) = jda.await<ButtonInteractionEvent> {
    it.channel == this
        && it.componentId == id
        && (user == null || it.user == user)
        && filter(it)
}

/**
 * Requires an EventManager implementation that supports either [EventListener] or [SubscribeEvent].
 *
 * Waits for button to be pressed/clicked in the target channel.
 *
 * ## Example
 *
 * ```kotlin
 * jda.onCommand("ban") { event ->
 *   val user = event.getOption("user")!!.asUser
 *   val button = Button.danger("${user.id}:confirm", "Confirm")
 *   event.reply("Are you sure you want to ban ${user.asMention}?")
 *     .addActionRow(button)
 *     .setEphemeral(true)
 *     .queue()
 *   val pressed = event.channel.awaitButton(button)
 *   pressed.deferEdit().queue() // don't forget to acknowledge
 *   event.guild.ban(user, 0).queue()
 * }
 * ```
 *
 * @param[button] The button (must not be link button)
 * @param[user] A specific user to filter for (optional)
 * @param[filter] A filter function to simplify code flow (optional)
 *
 * @return[ButtonInteractionEvent]
 */
suspend inline fun MessageChannel.awaitButton(button: Button, user: User? = null, crossinline filter: (ButtonInteractionEvent) -> Boolean = { true }) = awaitButton(checkNotNull(button.customId), user, filter)

/**
 * Requires an EventManager implementation that supports either [EventListener] or [SubscribeEvent].
 *
 * Waits for button to be pressed/clicked by the target user.
 *
 * ## Example
 *
 * ```kotlin
 * jda.onCommand("ban") { event ->
 *   val user = event.getOption("user")!!.asUser
 *   event.reply("Are you sure you want to ban ${user.asMention}?")
 *     .addActionRow(Button.danger("${user.id}:confirm", "Confirm"))
 *     .setEphemeral(true)
 *     .queue()
 *   val pressed = event.user.awaitButton("${user.id}:confirm")
 *   pressed.deferEdit().queue() // don't forget to acknowledge
 *   event.guild.ban(user, 0).queue()
 * }
 * ```
 *
 * @param[id] The button id
 * @param[filter] A filter function to simplify code flow (optional)
 *
 * @return[ButtonInteractionEvent]
 */
suspend inline fun User.awaitButton(id: String, crossinline filter: (ButtonInteractionEvent) -> Boolean = { true }) = jda.await<ButtonInteractionEvent> {
    it.user == this
        && it.componentId == id
        && filter(it)
}

/**
 * Requires an EventManager implementation that supports either [EventListener] or [SubscribeEvent].
 *
 * Waits for button to be pressed/clicked by the target user.
 *
 * ## Example
 *
 * ```kotlin
 * jda.onCommand("ban") { event ->
 *   val user = event.getOption("user")!!.asUser
 *   val button = Button.danger("${user.id}:confirm", "Confirm")
 *   event.reply("Are you sure you want to ban ${user.asMention}?")
 *     .addActionRow(button)
 *     .setEphemeral(true)
 *     .queue()
 *   val pressed = event.user.awaitButton(button)
 *   pressed.deferEdit().queue() // don't forget to acknowledge
 *   event.guild.ban(user, 0).queue()
 * }
 * ```
 *
 * @param[button] The button (must not be link button)
 * @param[filter] A filter function to simplify code flow (optional)
 *
 * @return[ButtonInteractionEvent]
 */
suspend inline fun User.awaitButton(button: Button, crossinline filter: (ButtonInteractionEvent) -> Boolean = { true }) = awaitButton(checkNotNull(button.customId), filter)

/**
 * Requires an EventManager implementation that supports either [EventListener] or [SubscribeEvent].
 *
 * Waits for button to be pressed/clicked by the target member.
 *
 * ## Example
 *
 * ```kotlin
 * jda.onCommand("ban") { event ->
 *   val user = event.getOption("user")!!.asUser
 *   event.reply("Are you sure you want to ban ${user.asMention}?")
 *     .addActionRow(Button.danger("${user.id}:confirm", "Confirm"))
 *     .setEphemeral(true)
 *     .queue()
 *   val pressed = event.member!!.awaitButton("${user.id}:confirm")
 *   pressed.deferEdit().queue() // don't forget to acknowledge
 *   event.guild.ban(user, 0).queue()
 * }
 * ```
 *
 * @param[id] The button id
 * @param[filter] A filter function to simplify code flow (optional)
 *
 * @return[ButtonInteractionEvent]
 */
suspend inline fun Member.awaitButton(id: String, crossinline filter: (ButtonInteractionEvent) -> Boolean = { true }) = jda.await<ButtonInteractionEvent> {
    it.member == this
        && it.componentId == id
        && filter(it)
}

/**
 * Requires an EventManager implementation that supports either [EventListener] or [SubscribeEvent].
 *
 * Waits for button to be pressed/clicked by the target member.
 *
 * ## Example
 *
 * ```kotlin
 * jda.onCommand("ban") { event ->
 *   val user = event.getOption("user")!!.asUser
 *   val button = Button.danger("${user.id}:confirm", "Confirm")
 *   event.reply("Are you sure you want to ban ${user.asMention}?")
 *     .addActionRow(button)
 *     .setEphemeral(true)
 *     .queue()
 *   val pressed = event.member!!.awaitButton(button)
 *   pressed.deferEdit().queue() // don't forget to acknowledge
 *   event.guild.ban(user, 0).queue()
 * }
 * ```
 *
 * @param[button] The button (must not be link button)
 * @param[filter] A filter function to simplify code flow (optional)
 *
 * @return[ButtonInteractionEvent]
 */
suspend inline fun Member.awaitButton(button: Button, crossinline filter: (ButtonInteractionEvent) -> Boolean = { true }) = awaitButton(checkNotNull(button.customId), filter)