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
import net.dv8tion.jda.api.interactions.callbacks.IMessageEditCallback
import net.dv8tion.jda.api.interactions.components.ActionRow
import net.dv8tion.jda.api.requests.restaction.MessageAction
import net.dv8tion.jda.api.requests.restaction.WebhookMessageUpdateAction
import net.dv8tion.jda.api.requests.restaction.interactions.MessageEditCallbackAction
import net.dv8tion.jda.internal.JDAImpl
import net.dv8tion.jda.internal.interactions.InteractionHookImpl
import net.dv8tion.jda.internal.requests.Route
import net.dv8tion.jda.internal.requests.restaction.MessageActionImpl
import net.dv8tion.jda.internal.requests.restaction.WebhookMessageUpdateActionImpl
import net.dv8tion.jda.internal.requests.restaction.interactions.MessageEditCallbackActionImpl

/**
 * Defaults used for edit message extensions provided by this module.
 * Each function that relies on these defaults, says so explicitly. This does not apply for send functions.
 *
 * These can be changed but keep in mind they will apply globally.
 */
object MessageEditDefaults {
    /**
     * Whether message edits should replace the entire message by default
     */
    var replace: Boolean = false
}

/**
 * Add a collection of [NamedFile] to this request.
 *
 * @param[files] The files to add
 */
fun MessageEditCallbackAction.addFiles(files: Files) {
    files.forEach {
        addFile(it.data, it.name, *it.options)
    }
}

/**
 * Add a collection of [NamedFile] to this request.
 *
 * @param[files] The files to add
 */
fun <T> WebhookMessageUpdateAction<T>.addFiles(files: Files) {
    files.forEach {
        addFile(it.data, it.name, *it.options)
    }
}


// Using an underscore at the end to prevent overload specialization
// You can remove it with an import alias (import ... as foo) but i would recommend against it

private inline fun <T> T.applyIf(check: Boolean, func: (T) -> Unit) {
    if (check || this != null) {
        func(this)
    }
}

private fun <T> allOf(first: T?, other: Collection<T>?): List<T>? {
    if (first == null && other == null)
        return null
    val list = mutableListOf<T>()
    first?.let { list.add(it) }
    other?.let { list.addAll(it) }
    return list
}

/**
 * Edit the original message from this interaction.
 *
 * This makes use of [MessageEditDefaults].
 *
 * This does not currently replace files but may do so in the future.
 *
 * @param[content] The message content
 * @param[embed] One embed
 * @param[embeds] Multiple embeds
 * @param[components] Components for this message
 * @param[file] One file
 * @param[files] Multiple files
 * @param[replace] Whether this should replace the entire message
 *
 * @return[MessageEditCallbackAction]
 */
fun IMessageEditCallback.editMessage_(
    content: String? = null,
    embed: MessageEmbed? = null,
    embeds: Embeds? = null,
    components: Components? = null,
    file: NamedFile? = null,
    files: Files? = null,
    replace: Boolean = MessageEditDefaults.replace
): MessageEditCallbackAction = MessageEditCallbackActionImpl(
    hook as InteractionHookImpl
).apply {
    content.applyIf(replace) {
        setContent(it)
    }

    components.applyIf(replace) {
        setActionRows(it?.mapNotNull { k -> k as? ActionRow } ?: emptyList())
    }

    allOf(embed, embeds).applyIf(replace) {
        setEmbeds(it ?: emptyList())
    }

    allOf(file, files).applyIf(true) {
        it?.let { addFiles(it) }
// TODO: waiting for https://github.com/discord/discord-api-docs/discussions/3335
//        if (replace)
//            retainFilesById(LongRange(0, it?.size?.toLong() ?: 0L).map(Long::toString).toList())
    }
}

/**
 * Edit a message from this interaction.
 *
 * This makes use of [MessageEditDefaults].
 *
 * This does not currently replace files but may do so in the future.
 *
 * @param[id] The message id, defaults to "@original"
 * @param[content] The message content
 * @param[embed] One embed
 * @param[embeds] Multiple embeds
 * @param[components] Components for this message
 * @param[file] One file
 * @param[files] Multiple files
 * @param[replace] Whether this should replace the entire message
 *
 * @return[WebhookMessageUpdateAction]
 */
@Suppress("MoveLambdaOutsideParentheses")
fun InteractionHook.editMessage(
    id: String = "@original",
    content: String? = null,
    embed: MessageEmbed? = null,
    embeds: Embeds? = null,
    components: Components? = null,
    file: NamedFile? = null,
    files: Files? = null,
    replace: Boolean = MessageEditDefaults.replace,
): WebhookMessageUpdateAction<Message> = WebhookMessageUpdateActionImpl(
    jda,
    Route.Interactions.EDIT_FOLLOWUP.compile(jda.selfUser.applicationId, interaction.token, id),
    { (jda as JDAImpl).entityBuilder.createMessage(it) }
).apply {
    content.applyIf(replace) {
        setContent(it)
    }

    components.applyIf(replace) {
        setActionRows(it?.mapNotNull { k -> k as? ActionRow } ?: emptyList())
    }

    allOf(embed, embeds).applyIf(replace) {
        setEmbeds(it ?: emptyList())
    }

    allOf(file, files).applyIf(true) {
        it?.let { addFiles(it) }
// TODO: Wait for support in JDA for new attachments behavior
//        if (replace)
//            retainFilesById(LongRange(0, it?.size?.toLong() ?: 0L).map(Long::toString).toList())
    }
}

/**
 * Edit a message from this channel.
 *
 * This makes use of [MessageEditDefaults].
 *
 * This does not currently replace files but may do so in the future.
 *
 * @param[id] The message id
 * @param[content] The message content
 * @param[embed] One embed
 * @param[embeds] Multiple embeds
 * @param[components] Components for this message
 * @param[file] One file
 * @param[files] Multiple files
 * @param[replace] Whether this should replace the entire message
 *
 * @return[MessageAction]
 */
fun MessageChannel.editMessage(
    id: String,
    content: String? = null,
    embed: MessageEmbed? = null,
    embeds: Embeds? = null,
    components: Components? = null,
    file: NamedFile? = null,
    files: Files? = null,
    replace: Boolean = MessageEditDefaults.replace,
): MessageAction = MessageActionImpl(jda, id, this).apply {
    override(replace)

    content?.let {
        content(it)
    }

    components?.let {
        setActionRows(it.mapNotNull { k -> k as? ActionRow })
    }

    allOf(embed, embeds)?.let {
        setEmbeds(it)
    }

    allOf(file, files)?.let {
        addFiles(it)
// TODO: Wait for support in JDA for new attachments behavior
//        if (replace)
//            retainFilesById(LongRange(0, it.size.toLong()).map(Long::toString).toList())
    }
}

/**
 * Edit the message.
 *
 * This makes use of [MessageEditDefaults].
 *
 * This does not currently replace files but may do so in the future.
 *
 * @param[content] The message content
 * @param[embed] One embed
 * @param[embeds] Multiple embeds
 * @param[components] Components for this message
 * @param[file] One file
 * @param[files] Multiple files
 * @param[replace] Whether this should replace the entire message
 *
 * @return[MessageAction]
 */
fun Message.edit(
    content: String? = null,
    embed: MessageEmbed? = null,
    embeds: Embeds? = null,
    components: Components? = null,
    file: NamedFile? = null,
    files: Files? = null,
    replace: Boolean = MessageEditDefaults.replace,
) = channel.editMessage(id, content, embed, embeds, components, file, files, replace).reference(this)