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

import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.MessageChannel
import net.dv8tion.jda.api.entities.User
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
