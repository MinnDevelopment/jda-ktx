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

import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.interactions.components.ActionRow
import net.dv8tion.jda.api.interactions.components.Component
import net.dv8tion.jda.api.interactions.components.ComponentLayout
import net.dv8tion.jda.api.utils.AttachmentOption
import java.io.File
import java.io.InputStream

typealias Components = Collection<ComponentLayout>
typealias Embeds = Collection<MessageEmbed>
typealias Files = Collection<NamedFile>

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