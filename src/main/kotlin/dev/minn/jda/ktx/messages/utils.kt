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
import net.dv8tion.jda.api.components.actionrow.ActionRow
import net.dv8tion.jda.api.components.actionrow.ActionRowChildComponent
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.utils.FileUpload
import java.io.File
import java.io.InputStream

/**
 * Converts the collection of components into a collection of a single [ActionRow].
 *
 * @param[T] The component type such as button
 *
 * @return[List] of [ActionRow]
 */
@JvmName("intoComponents")
fun <T : ActionRowChildComponent> Collection<T>.into() = listOf(this.row())

/**
 * Wraps the component into a collection of a single [ActionRow].
 *
 * @return[List] of [ActionRow]
 */
fun ActionRowChildComponent.into() = row(this).into()

/**
 * Wraps the component layout into a collection of layouts.
 *
 * @return[List] of [ActionRow]
 */
fun ActionRow.into() = listOf(this)

/**
 * Wraps the embed into a collection of embeds.
 *
 * @return[List] of [MessageEmbed]
 */
fun MessageEmbed.into() = listOf(this)

/**
 * Wraps the file upload into a collection of file uploads.
 *
 * @return[List] of [FileUpload]
 */
fun FileUpload.into() = listOf(this)

// Lots of conversion methods you can use to convert your collections to named files
//
// mapOf(
//   "cat.gif" to File("123.gif"),
//   "thing.txt" to File("thing.txt")
// ).into() // -> Collection<NamedFile> = Files

/**
 * Converts a collection of [File] into a [List] of [FileUpload].
 *
 * @return[List] of [FileUpload]
 */
@JvmName("intoNamedFile")
fun Collection<File>.into() = map { FileUpload.fromData(it) }

/**
 * Converts this map to a [List] of [FileUpload].
 * This will use the keys as file names.
 *
 * @return[List] of [FileUpload]
 */
@JvmName("mapFilesIntoNamedFiles")
fun Map<String, File>.into() = map { FileUpload.fromData(it.value, it.key) }

/**
 * Converts this map to a [List] of [FileUpload].
 * This will use the keys as file names.
 *
 * @return[List] of [FileUpload]
 */
@JvmName("mapStreamsIntoNamedFiles")
fun Map<String, InputStream>.into() = map { FileUpload.fromData(it.value, it.key) }

/**
 * Converts this map to a [List] of [FileUpload].
 * This will use the keys as file names.
 *
 * @return[List] of [FileUpload]
 */
@JvmName("mapArrayIntoNamedFiles")
fun Map<String, ByteArray>.into() = map { FileUpload.fromData(it.value, it.key) }


// fun eval(code: String): EvalResult { ... }
//
// val (stdout, stderr) = eval(code)
// val outputs = listOf(stdout.named("stdout.txt"), stderr.named("stderr.txt"))
// event.reply_(files=outputs).queue()

/**
 * Wraps this InputStream in a [FileUpload]
 *
 * @param[name] The name of the file
 *
 * @return[FileUpload]
 */
fun InputStream.named(name: String) = FileUpload.fromData(this, name)

/**
 * Wraps this ByteArray in a [FileUpload]
 *
 * @param[name] The name of the file
 *
 * @return[FileUpload]
 */
fun ByteArray.named(name: String) = FileUpload.fromData(this, name)

/**
 * Wraps this File in a [FileUpload]
 *
 * @param[name] The name of the file
 *
 * @return[FileUpload]
 *
 * @see[File.into]
 */
fun File.named(name: String) = FileUpload.fromData(this, name)

// val outputs = listOf("stdout.txt"(stdout), "stderr"(stderr))
// there are kind of a meme, no need to document tbh

operator fun String.invoke(file: InputStream) = file.named(this)
operator fun String.invoke(file: ByteArray) = file.named(this)
operator fun String.invoke(file: File) = file.named(this)

// If you want to just use the file name

/**
 * Wraps this File in a [FileUpload]
 *
 * @return[FileUpload]
 *
 * @see[File.named]
 */
fun File.into() = listOf(this).into()
