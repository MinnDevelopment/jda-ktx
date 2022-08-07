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

package dev.minn.jda.ktx.messages

import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.MessageChannel
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.interactions.InteractionHook
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback
import net.dv8tion.jda.api.interactions.components.LayoutComponent
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction
import net.dv8tion.jda.api.requests.restaction.WebhookMessageCreateAction
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction
import net.dv8tion.jda.api.utils.FileUpload

// Defaults for keyword arguments
/**
 * Defaults used for send and reply extensions provided by this module.
 * Each function that relies on these defaults, says so explicitly. This does not apply for edit functions.
 *
 * These can be changed but keep in mind they will apply globally.
 */
object SendDefaults {
    /**
     * The default message content.
     */
    var content: String = ""

    /**
     * The default message embeds
     */
    var embeds: Collection<MessageEmbed> = emptyList()

    /**
     * The default message components
     */
    var components: Collection<LayoutComponent> = emptyList()

    /**
     * The default ephemeral state for interactions
     */
    var ephemeral: Boolean = false
    // Not supporting files since input streams are single use, and I don't think its worth it
}

// Using an underscore at the end to prevent overload specialization
// You can remove it with an import alias (import ... as foo) but i would recommend against it

/**
 * Send a reply to this interaction.
 *
 * This makes use of [SendDefaults].
 *
 * @param[content] The message content
 * @param[embeds] Multiple embeds
 * @param[components] Components for this message
 * @param[files] Multiple files
 * @param[ephemeral] Whether this message is ephemeral
 *
 * @return[ReplyCallbackAction]
 *
 * @see  [IReplyCallback.deferReply]
 */
fun IReplyCallback.reply_(
    content: String = SendDefaults.content,
    embeds: Collection<MessageEmbed> = SendDefaults.embeds,
    components: Collection<LayoutComponent> = SendDefaults.components,
    files: Collection<FileUpload> = emptyList(),
    tts: Boolean = false,
    mentions: Mentions = Mentions.default(),
    ephemeral: Boolean = SendDefaults.ephemeral,
): ReplyCallbackAction = deferReply(ephemeral).applyData(MessageCreate(content, embeds, files, components, tts, mentions))

/**
 * Send a reply to this interaction.
 *
 * This makes use of [SendDefaults].
 *
 * @param[content] The message content
 * @param[embeds] Multiple embeds
 * @param[components] Components for this message
 * @param[files] Multiple files
 * @param[ephemeral] Whether this message is ephemeral
 *
 * @return[WebhookMessageCreateAction]
 *
 * @see  [InteractionHook.sendMessage]
 */
fun InteractionHook.send(
    content: String = SendDefaults.content,
    embeds: Collection<MessageEmbed> = SendDefaults.embeds,
    components: Collection<LayoutComponent> = SendDefaults.components,
    files: Collection<FileUpload> = emptyList(),
    tts: Boolean = false,
    mentions: Mentions = Mentions.default(),
    ephemeral: Boolean = SendDefaults.ephemeral,
) = sendMessage(MessageCreate(content, embeds, files, components, tts, mentions)).setEphemeral(ephemeral)

/**
 * Send a message in this channel.
 *
 * This makes use of [SendDefaults].
 *
 * @param[content] The message content
 * @param[embeds] Multiple embeds
 * @param[components] Components for this message
 * @param[files] Multiple files
 *
 * @return[MessageCreateAction]
 */
fun MessageChannel.send(
    content: String = SendDefaults.content,
    embeds: Collection<MessageEmbed> = SendDefaults.embeds,
    components: Collection<LayoutComponent> = SendDefaults.components,
    files: Collection<FileUpload> = emptyList(),
    tts: Boolean = false,
    mentions: Mentions = Mentions.default(),
): MessageCreateAction = sendMessage(MessageCreate(content, embeds, files, components, tts, mentions))

/**
 * Send a reply to this message in the same channel.
 *
 * This makes use of [SendDefaults].
 *
 * @param[content] The message content
 * @param[embeds] Multiple embeds
 * @param[components] Components for this message
 * @param[files] Multiple files
 *
 * @return[MessageCreateAction]
 */
fun Message.reply_(
    content: String = SendDefaults.content,
    embeds: Collection<MessageEmbed> = SendDefaults.embeds,
    components: Collection<LayoutComponent> = SendDefaults.components,
    files: Collection<FileUpload> = emptyList(),
    tts: Boolean = false,
    mentions: Mentions = Mentions.default(),
) = channel.send(content, embeds, components, files, tts, mentions).setMessageReference(this)
