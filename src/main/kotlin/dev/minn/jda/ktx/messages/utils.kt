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

import dev.minn.jda.ktx.interactions.components.row
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.interactions.components.ActionRow
import net.dv8tion.jda.api.interactions.components.ItemComponent
import net.dv8tion.jda.api.interactions.components.LayoutComponent
import java.io.File
import java.io.InputStream

typealias Components = Collection<LayoutComponent>
typealias Embeds = Collection<MessageEmbed>
typealias Files = Collection<NamedFile>

/**
 * A custom data class used to represent named files for message attachments.
 *
 * ## Example
 *
 * ```kt
 * val files: Collection<NamedFile> = listOf(File("cat.gif"), File("dog.jpg")).into()
 * val file: NamedFile = File("cat.gif").named("notcat.gif")
 * ```
 *
 * @param[name] The filename to use
 * @param[data] The file contents as an input stream
 *
 * @see  [File.into]
 * @see  [File.named]
 * @see  [InputStream.named]
 * @see  [ByteArray.named]
 */
data class NamedFile(
    val name: String,
    val data: InputStream
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as NamedFile

        if (name != other.name) return false
        if (data != other.data) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + data.hashCode()
        return result
    }
}


/**
 * Converts the collection of components into a collection of a single [ActionRow].
 *
 * @param[T] The component type such as button
 *
 * @return[List] of [ActionRow]
 */
@JvmName("intoComponents")
fun <T : ItemComponent> Collection<T>.into() = listOf(this.row())

/**
 * Wraps the component into a collection of a single [ActionRow].
 *
 * @return[List] of [ActionRow]
 */
fun ItemComponent.into() = row(this).into()

/**
 * Wraps the component layout into a collection of layouts.
 *
 * @return[List] of [LayoutComponent]
 */
fun LayoutComponent.into() = listOf(this)

// Lots of conversion methods you can use to convert your collections to named files
//
// mapOf(
//   "cat.gif" to File("123.gif"),
//   "thing.txt" to File("thing.txt")
// ).into() // -> Collection<NamedFile> = Files

/**
 * Converts a collection of [File] into a [List] or [NamedFile].
 *
 * @return[List] of [NamedFile]
 */
@JvmName("intoNamedFile")
fun Collection<File>.into() = map { NamedFile(it.name, it.inputStream()) }

/**
 * Converts this map to a [List] of [NamedFile].
 * This will use the keys as file names.
 *
 * @return[List] of [NamedFile]
 */
@JvmName("mapFilesIntoNamedFiles")
fun Map<String, File>.into() = map { NamedFile(it.key, it.value.inputStream()) }
/**
 * Converts this map to a [List] of [NamedFile].
 * This will use the keys as file names.
 *
 * @return[List] of [NamedFile]
 */
@JvmName("mapStreamsIntoNamedFiles")
fun Map<String, InputStream>.into() = map { NamedFile(it.key, it.value) }
/**
 * Converts this map to a [List] of [NamedFile].
 * This will use the keys as file names.
 *
 * @return[List] of [NamedFile]
 */
@JvmName("mapArrayIntoNamedFiles")
fun Map<String, ByteArray>.into() = map { NamedFile(it.key, it.value.inputStream()) }


// fun eval(code: String): EvalResult { ... }
//
// val (stdout, stderr) = eval(code)
// val outputs = listOf(stdout.named("stdout.txt"), stderr.named("stderr.txt"))
// event.reply_(files=outputs).queue()

/**
 * Wraps this InputStream in a [NamedFile]
 *
 * @param[name] The name of the file
 * @param[options] The attachment options
 *
 * @return[NamedFile]
 */
fun InputStream.named(name: String) = NamedFile(name, this)

/**
 * Wraps this ByteArray in a [NamedFile]
 *
 * @param[name] The name of the file
 *
 * @return[NamedFile]
 */
fun ByteArray.named(name: String) = NamedFile(name, this.inputStream())

/**
 * Wraps this File in a [NamedFile]
 *
 * @param[name] The name of the file
 * @param[options] The attachment options
 *
 * @return[NamedFile]
 *
 * @see[File.into]
 */
fun File.named(name: String) = NamedFile(name, this.inputStream())

// val outputs = listOf("stdout.txt"(stdout), "stderr"(stderr))
// there are kind of a meme, no need to document tbh

operator fun String.invoke(file: InputStream) = file.named(this)
operator fun String.invoke(file: ByteArray) = file.named(this)
operator fun String.invoke(file: File) = file.named(this)

// If you want to just use the file name

/**
 * Wraps this File in a [NamedFile]
 *
 * @return[NamedFile]
 *
 * @see[File.named]
 */
fun File.into() = listOf(this).into()

fun <T> allOf(first: T?, other: Collection<T>?): List<T>? {
    if (first == null && other == null)
        return null
    val list = mutableListOf<T>()
    first?.let { list.add(it) }
    other?.let { list.addAll(it) }
    return list
}