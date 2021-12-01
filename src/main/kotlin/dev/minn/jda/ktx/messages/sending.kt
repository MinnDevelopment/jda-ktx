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
import net.dv8tion.jda.api.requests.restaction.MessageAction
import net.dv8tion.jda.api.requests.restaction.WebhookMessageAction
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyAction
import net.dv8tion.jda.internal.JDAImpl
import net.dv8tion.jda.internal.interactions.InteractionHookImpl
import net.dv8tion.jda.internal.requests.Route
import net.dv8tion.jda.internal.requests.restaction.MessageActionImpl
import net.dv8tion.jda.internal.requests.restaction.WebhookMessageActionImpl
import net.dv8tion.jda.internal.requests.restaction.interactions.ReplyActionImpl

// Defaults for keyword arguments
object SendDefaults {
    var content: String? = null
    var embeds: Embeds = emptyList()
    var components: Components = emptyList()
    var ephemeral: Boolean = false
    // Not supporting files since input streams are single use and I don't think its worth it
}

// And you can also just add these to the individual actions easily

fun MessageAction.addFiles(files: Files) {
    files.forEach {
        addFile(it.data, it.name, *it.options)
    }
}

fun ReplyAction.addFiles(files: Files) {
    files.forEach {
        addFile(it.data, it.name, *it.options)
    }
}

fun <T> WebhookMessageAction<T>.addFiles(files: Files) {
    files.forEach {
        addFile(it.data, it.name, *it.options)
    }
}

// Using an underscore at the end to prevent overload specialization
// You can remove it with an import alias (import ... as foo) but i would recommend against it

fun Interaction.reply_(
    content: String? = SendDefaults.content,
    embed: MessageEmbed? = null,
    embeds: Embeds = SendDefaults.embeds,
    components: Components = SendDefaults.components,
    file: NamedFile? = null,
    files: Files = emptyList(),
    ephemeral: Boolean = SendDefaults.ephemeral,
) = ReplyActionImpl(hook as InteractionHookImpl).apply {
    setContent(content)
    setEphemeral(ephemeral)

    if (components.isNotEmpty()) {
        addActionRows(components.mapNotNull { it as? ActionRow })
    }

    if (embed != null) {
        addEmbeds(embed)
    }

    if (embeds.isNotEmpty()) {
        addEmbeds(embeds)
    }

    file?.let { addFile(it.data, it.name, *it.options) }
    addFiles(files)
}

@Suppress("MoveLambdaOutsideParentheses")
fun InteractionHook.send(
    content: String? = SendDefaults.content,
    embed: MessageEmbed? = null,
    embeds: Embeds = SendDefaults.embeds,
    components: Components = SendDefaults.components,
    file: NamedFile? = null,
    files: Files = emptyList(),
    ephemeral: Boolean = SendDefaults.ephemeral,
) = WebhookMessageActionImpl(
    jda,
    interaction.messageChannel,
    Route.Interactions.CREATE_FOLLOWUP.compile(jda.selfUser.applicationId, interaction.token),
    { (jda as JDAImpl).entityBuilder.createMessage(it) }
).apply {
    setContent(content)
    setEphemeral(ephemeral)

    if (components.isNotEmpty()) {
        addActionRows(components.mapNotNull { it as? ActionRow })
    }

    if (embed != null) {
        addEmbeds(embed)
    }

    if (embeds.isNotEmpty()) {
        addEmbeds(embeds)
    }

    file?.let { addFile(it.data, it.name, *it.options) }
    addFiles(files)
}

fun MessageChannel.send(
    content: String? = SendDefaults.content,
    embed: MessageEmbed? = null,
    embeds: Embeds = SendDefaults.embeds,
    components: Components = SendDefaults.components,
    file: NamedFile? = null,
    files: Files = emptyList(),
) = MessageActionImpl(jda, null, this).apply {
    content(content)

    if (components.isNotEmpty()) {
        setActionRows(components.mapNotNull { it as? ActionRow })
    }

    if (embed != null) {
        setEmbeds(embed)
    }

    if (embeds.isNotEmpty()) {
        setEmbeds(embeds)
    }

    file?.let { addFile(it.data, it.name, *it.options) }
    addFiles(files)
}

fun Message.reply_(
    content: String? = SendDefaults.content,
    embed: MessageEmbed? = null,
    embeds: Embeds = SendDefaults.embeds,
    components: Components = SendDefaults.components,
    file: NamedFile? = null,
    files: Files = emptyList(),
) = channel.send(content, embed, embeds, components, file, files).reference(this)
