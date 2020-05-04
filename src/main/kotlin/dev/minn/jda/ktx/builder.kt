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

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.MessageBuilder
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.MessageEmbed
import java.time.temporal.TemporalAccessor

fun message(
    content: String? = null,
    embed: MessageEmbed? = null,
    nonce: String? = null,
    tts: Boolean = false,
    builder: MessageBuilder.() -> Unit = {}
): Message {
    return MessageBuilder().run {
        setContent(content)
        setEmbed(embed)
        setNonce(nonce)
        setTTS(tts)
        builder()
        build()
    }
}

fun embed(
    description: String? = null,
    title: String? = null,
    url: String? = null,
    color: Int? = null,
    footerText: String? = null,
    footerIcon: String? = null,
    authorName: String? = null,
    authorIcon: String? = null,
    authorUrl: String? = null,
    timestamp: TemporalAccessor? = null,
    image: String? = null,
    thumbnail: String? = null,
    fields: Collection<MessageEmbed.Field> = emptyList(),
    builder: EmbedBuilder.() -> Unit = {}
): MessageEmbed {
    return EmbedBuilder().run {
        setDescription(description)
        setTitle(title, url)
        setFooter(footerText, footerIcon)
        setAuthor(authorName, authorUrl, authorIcon)
        setTimestamp(timestamp)
        setThumbnail(thumbnail)
        setImage(image)
        fields.map(this::addField)
        color?.let(this::setColor)
        builder()
        build()
    }
}