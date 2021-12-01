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
import net.dv8tion.jda.api.interactions.Interaction
import net.dv8tion.jda.api.interactions.InteractionHook
import net.dv8tion.jda.api.interactions.components.ActionRow
import net.dv8tion.jda.api.requests.restaction.WebhookMessageUpdateAction
import net.dv8tion.jda.api.requests.restaction.interactions.UpdateInteractionAction
import net.dv8tion.jda.internal.JDAImpl
import net.dv8tion.jda.internal.interactions.InteractionHookImpl
import net.dv8tion.jda.internal.requests.Route
import net.dv8tion.jda.internal.requests.restaction.MessageActionImpl
import net.dv8tion.jda.internal.requests.restaction.WebhookMessageUpdateActionImpl
import net.dv8tion.jda.internal.requests.restaction.interactions.UpdateInteractionActionImpl

object MessageEditDefaults {
    var replace: Boolean = false
}

fun UpdateInteractionAction.addFiles(files: Files) {
    files.forEach {
        addFile(it.data, it.name, *it.options)
    }
}

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

fun Interaction.edit(
    content: String? = null,
    embed: MessageEmbed? = null,
    embeds: Embeds? = null,
    components: Components? = null,
    file: NamedFile? = null,
    files: Files? = null,
    replace: Boolean = MessageEditDefaults.replace
) = UpdateInteractionActionImpl(hook as InteractionHookImpl).apply {
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
) = WebhookMessageUpdateActionImpl(
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

fun MessageChannel.editMessage(
    id: String,
    content: String? = null,
    embed: MessageEmbed? = null,
    embeds: Embeds? = null,
    components: Components? = null,
    file: NamedFile? = null,
    files: Files? = null,
    replace: Boolean = MessageEditDefaults.replace,
) = MessageActionImpl(jda, id, this).apply {
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

fun Message.edit(
    content: String? = null,
    embed: MessageEmbed? = null,
    embeds: Embeds? = null,
    components: Components? = null,
    file: NamedFile? = null,
    files: Files? = null,
    replace: Boolean = MessageEditDefaults.replace,
) = channel.editMessage(id, content, embed, embeds, components, file, files, replace).reference(this)