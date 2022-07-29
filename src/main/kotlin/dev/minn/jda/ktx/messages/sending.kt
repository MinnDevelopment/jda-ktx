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
import net.dv8tion.jda.api.interactions.components.ActionRow
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction
import net.dv8tion.jda.api.requests.restaction.WebhookMessageAction
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
    var content: String? = null

    /**
     * The default message embeds
     */
    var embeds: Embeds = emptyList()

    /**
     * The default message components
     */
    var components: Components = emptyList()

    /**
     * The default ephemeral state for interactions
     */
    var ephemeral: Boolean = false
    // Not supporting files since input streams are single use, and I don't think its worth it
}

// And you can also just add these to the individual actions easily

///**
// * Add a collection of [NamedFile] to this request.
// *
// * @param[files] The files to add
// */
//fun MessageAction.addFiles(files: Files) = apply {
//    files.forEach {
//        addFile(it.data, it.name, *it.options)
//    }
//}
//
///**
// * Add a collection of [NamedFile] to this request.
// *
// * @param[files] The files to add
// */
//fun ReplyCallbackAction.addFiles(files: Files) = apply {
//    files.forEach {
//        addFile(it.data, it.name, *it.options)
//    }
//}
//
///**
// * Add a collection of [NamedFile] to this request.
// *
// * @param[files] The files to add
// */
//fun <T> WebhookMessageAction<T>.addFiles(files: Files) = apply {
//    files.forEach {
//        addFile(it.data, it.name, *it.options)
//    }
//}

// Using an underscore at the end to prevent overload specialization
// You can remove it with an import alias (import ... as foo) but i would recommend against it

/**
 * Send a reply to this interaction.
 *
 * This makes use of [SendDefaults].
 *
 * @param[content] The message content
 * @param[embed] One embed
 * @param[embeds] Multiple embeds
 * @param[components] Components for this message
 * @param[file] One file
 * @param[files] Multiple files
 * @param[ephemeral] Whether this message is ephemeral
 *
 * @see  [IReplyCallback.deferReply]
 */
fun IReplyCallback.reply_(
    content: String? = SendDefaults.content,
    embed: MessageEmbed? = null,
    embeds: Embeds = SendDefaults.embeds,
    components: Components = SendDefaults.components,
    file: NamedFile? = null,
    files: Files = emptyList(),
    ephemeral: Boolean = SendDefaults.ephemeral,
): ReplyCallbackAction = deferReply().apply {
    setContent(content)
    setEphemeral(ephemeral)

    if (components.isNotEmpty()) {
        addComponents(components.mapNotNull { it as? ActionRow })
    }

    if (embed != null) {
        addEmbeds(embed)
    }

    if (embeds.isNotEmpty()) {
        addEmbeds(embeds)
    }

    file?.let { addFiles(FileUpload.fromData(it.data, it.name)) }
    addFiles(files)
}

/**
 * Send a reply to this interaction.
 *
 * This makes use of [SendDefaults].
 *
 * @param[content] The message content
 * @param[embed] One embed
 * @param[embeds] Multiple embeds
 * @param[components] Components for this message
 * @param[file] One file
 * @param[files] Multiple files
 * @param[ephemeral] Whether this message is ephemeral
 *
 * @return[WebhookMessageAction]
 *
 * @see  [InteractionHook.sendMessage]
 */
fun InteractionHook.send(
    content: String? = SendDefaults.content,
    embed: MessageEmbed? = null,
    embeds: Embeds = SendDefaults.embeds,
    components: Components = SendDefaults.components,
    file: NamedFile? = null,
    files: Files = emptyList(),
    ephemeral: Boolean = SendDefaults.ephemeral,
) = sendMessage("tmp").apply {
    setContent(content)
    setEphemeral(ephemeral)

    if (components.isNotEmpty()) {
        addComponents(components)
    }

    if (embed != null) {
        addEmbeds(embed)
    }

    if (embeds.isNotEmpty()) {
        addEmbeds(embeds)
    }

    file?.let { addFiles(FileUpload.fromData(it.data, it.name)) }
    addFiles(files)
}

/**
 * Send a message in this channel.
 *
 * This makes use of [SendDefaults].
 *
 * @param[content] The message content
 * @param[embed] One embed
 * @param[embeds] Multiple embeds
 * @param[components] Components for this message
 * @param[file] One file
 * @param[files] Multiple files
 *
 * @return[MessageAction]
 */
fun MessageChannel.send(
    content: String? = SendDefaults.content,
    embed: MessageEmbed? = null,
    embeds: Embeds = SendDefaults.embeds,
    components: Components = SendDefaults.components,
    file: NamedFile? = null,
    files: Files = emptyList(),
): MessageCreateAction = sendMessage("tmp").apply {
    setContent(content)

    if (components.isNotEmpty()) {
        addComponents(components.mapNotNull { it as? ActionRow })
    }

    if (embed != null) {
        setEmbeds(embed)
    }

    if (embeds.isNotEmpty()) {
        setEmbeds(embeds)
    }

    file?.let { addFiles(FileUpload.fromData(it.data, it.name)) }
    addFiles(files)
}

/**
 * Send a reply to this message in the same channel.
 *
 * This makes use of [SendDefaults].
 *
 * @param[content] The message content
 * @param[embed] One embed
 * @param[embeds] Multiple embeds
 * @param[components] Components for this message
 * @param[file] One file
 * @param[files] Multiple files
 *
 * @return[MessageAction]
 */
fun Message.reply_(
    content: String? = SendDefaults.content,
    embed: MessageEmbed? = null,
    embeds: Embeds = SendDefaults.embeds,
    components: Components = SendDefaults.components,
    file: NamedFile? = null,
    files: Files = emptyList(),
) = channel.send(content, embed, embeds, components, file, files).setMessageReference(this)
