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
import net.dv8tion.jda.api.requests.restaction.MessageAction
import net.dv8tion.jda.api.requests.restaction.WebhookMessageAction
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyAction
import net.dv8tion.jda.api.requests.restaction.interactions.UpdateInteractionAction
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

// Defaults for keyword arguments
object SendDefaults {
    var content: String? = null
    var embeds: Embeds = emptyList()
    var components: Components = emptyList()
    var ephemeral: Boolean = false
    // Not supporting files since input streams are single use and I don't think its worth it
}

// Custom data class used to make sending files simpler
data class NamedFile(
    val name: String,
    val data: InputStream,
    val options: Array<out AttachmentOption> = emptyArray()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as NamedFile

        if (name != other.name) return false
        if (data != other.data) return false
        if (!options.contentEquals(other.options)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + data.hashCode()
        result = 31 * result + options.contentHashCode()
        return result
    }
}


@JvmName("intoComponents")
fun <T : Component> Collection<T>.into() = listOf(ActionRow.of(this))
fun Component.into() = listOf(this).into()
fun ComponentLayout.into() = listOf(this)

// Lots of conversion methods you can use to convert your collections to named files
//
// mapOf(
//   "cat.gif" to File("123.gif"),
//   "thing.txt" to File("thing.txt")
// ).into() // -> Collection<NamedFile> = Files

@JvmName("intoNamedFile")
fun Collection<File>.into() = map { NamedFile(it.name, it.inputStream()) }
@JvmName("mapFilesIntoNamedFiles")
fun Map<String, File>.into() = map { NamedFile(it.key, it.value.inputStream()) }
@JvmName("mapStreamsIntoNamedFiles")
fun Map<String, InputStream>.into() = map { NamedFile(it.key, it.value) }
@JvmName("mapArrayIntoNamedFiles")
fun Map<String, ByteArray>.into() = map { NamedFile(it.key, it.value.inputStream()) }


// fun eval(code: String): EvalResult { ... }
//
// val (stdout, stderr) = eval(code)
// val outputs = listOf(stdout.named("stdout.txt"), stderr.named("stderr.txt"))
// event.reply_(files=outputs).queue()

fun InputStream.named(name: String, vararg options: AttachmentOption) = NamedFile(name, this, options)
fun ByteArray.named(name: String, vararg options: AttachmentOption) = NamedFile(name, this.inputStream(), options)
fun File.named(name: String, vararg options: AttachmentOption) = NamedFile(name, this.inputStream(), options)

// val outputs = listOf("stdout.txt"(stdout), "stderr"(stderr))

operator fun String.invoke(file: InputStream, vararg options: AttachmentOption) = file.named(this, *options)
operator fun String.invoke(file: ByteArray, vararg options: AttachmentOption) = file.named(this, *options)
operator fun String.invoke(file: File, vararg options: AttachmentOption) = file.named(this, *options)

// If you want to just use the file name
fun File.into() = listOf(this).into()

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

fun UpdateInteractionAction.addFiles(files: Files) {
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
