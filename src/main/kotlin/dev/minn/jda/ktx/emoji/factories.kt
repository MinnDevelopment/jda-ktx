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

package dev.minn.jda.ktx.emoji

import net.dv8tion.jda.api.entities.Message.MentionType
import net.dv8tion.jda.api.entities.emoji.Emoji

/**
 * Parses the emoji instance from this format-string.
 *
 * For custom emoji, this should be using the markdown format such as `<:name:id>`.
 *
 * Unicode emoji can be either the unicode characters or the codepoint notation (ie. `U+XXXX`).
 *
 * @return The emoji instance.
 */
fun String.toEmoji() = Emoji.fromFormatted(this)

/**
 * Parses the custom emoji instance from this format-string.
 *
 * This should be using the markdown format such as `<:name:id>`.
 *
 * @throws[IllegalArgumentException] If the string is not a valid emoji format
 * @throws[NumberFormatException] If the id is not a valid long
 *
 * @return The custom emoji instance.
 */
fun String.toCustomEmoji() = MentionType.EMOJI.pattern.toRegex().matchEntire(this).run {
    if (this == null)
        throw IllegalArgumentException("The given string is not a custom emoji. Format must be `<:name:id>`.")
    val (name, id) = groupValues
    val animated = this@toCustomEmoji.startsWith("<a:")
    Emoji.fromCustom(name, id.toLong(), animated)
}

/**
 * Parses the unicode emoji instance
 *
 * This can be either the unicode characters or the codepoint notation (ie. `U+XXXX`).
 *
 * @return The unicode emoji instance.
 */
fun String.toUnicodeEmoji() = Emoji.fromUnicode(this)