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
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.interactions.Interaction
import net.dv8tion.jda.api.interactions.InteractionHook
import net.dv8tion.jda.api.interactions.components.ActionRow
import net.dv8tion.jda.api.interactions.components.Component
import net.dv8tion.jda.api.interactions.components.ComponentLayout
import net.dv8tion.jda.api.utils.AttachmentOption
import net.dv8tion.jda.internal.JDAImpl
import net.dv8tion.jda.internal.interactions.InteractionHookImpl
import net.dv8tion.jda.internal.requests.Route
import net.dv8tion.jda.internal.requests.restaction.MessageActionImpl
import net.dv8tion.jda.internal.requests.restaction.WebhookMessageActionImpl
import net.dv8tion.jda.internal.requests.restaction.interactions.ReplyActionImpl
import java.io.File
import java.io.InputStream

typealias Components = Collection<ComponentLayout>
typealias Embeds = Collection<MessageEmbed>
typealias Files = Collection<NamedFile>

object SendDefaults {
    var content: String? = null
    var embed: MessageEmbed? = null
    var embeds: Embeds = emptyList()
    var components: Components = emptyList()
    var ephemeral: Boolean = false
}

data class NamedFile(
    val name: String,
    val data: InputStream,
    val options: Collection<AttachmentOption> = emptyList()
)


@JvmName("intoComponents")
fun <T : Component> Collection<T>.into() = listOf(ActionRow.of(this))
fun Component.into() = listOf(this).into()
fun ComponentLayout.into() = listOf(this)

@JvmName("intoNamedFile")
fun Collection<File>.into() = map { NamedFile(it.name, it.inputStream()) }
@JvmName("mapFilesIntoNamedFiles")
fun Map<String, File>.into() = map { NamedFile(it.key, it.value.inputStream()) }
@JvmName("mapStreamsIntoNamedFiles")
fun Map<String, InputStream>.into() = map { NamedFile(it.key, it.value) }
@JvmName("mapArrayIntoNamedFiles")
fun Map<String, ByteArray>.into() = map { NamedFile(it.key, it.value.inputStream()) }

fun File.into() = listOf(this).into()


fun Interaction.reply(
    content: String? = SendDefaults.content,
    embed: MessageEmbed? = SendDefaults.embed,
    embeds: Embeds = SendDefaults.embeds,
    components: Components = SendDefaults.components,
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

    files.forEach {
        addFile(it.data, it.name, *it.options.toTypedArray())
    }
}

@Suppress("MoveLambdaOutsideParentheses")
fun InteractionHook.send(
    content: String? = SendDefaults.content,
    embed: MessageEmbed? = SendDefaults.embed,
    embeds: Embeds = SendDefaults.embeds,
    components: Components = SendDefaults.components,
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

    files.forEach {
        addFile(it.data, it.name, *it.options.toTypedArray())
    }
}

fun MessageChannel.send(
    content: String? = SendDefaults.content,
    embed: MessageEmbed? = SendDefaults.embed,
    embeds: Embeds = SendDefaults.embeds,
    components: Components = SendDefaults.components,
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

    files.forEach {
        addFile(it.data, it.name, *it.options.toTypedArray())
    }
}

fun Message.reply(
    content: String? = SendDefaults.content,
    embed: MessageEmbed? = SendDefaults.embed,
    embeds: Embeds = SendDefaults.embeds,
    components: Components = SendDefaults.components,
    files: Files = emptyList(),
) = channel.send(content, embed, embeds, components, files).reference(this)
