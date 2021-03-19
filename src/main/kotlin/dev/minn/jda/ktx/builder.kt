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

@file:Suppress("FunctionName")

package dev.minn.jda.ktx

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.MessageBuilder
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.entities.Role
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.requests.restaction.MessageAction
import java.time.temporal.TemporalAccessor

fun Message(
    content: String? = null,
    embed: MessageEmbed? = null,
    nonce: String? = null,
    tts: Boolean = false,
    allowedMentionTypes: Collection<Message.MentionType>? = null,
    mentionUsers: Collection<Long>? = null,
    mentionRoles: Collection<Long>? = null,
    builder: InlineMessage.() -> Unit = {}
): Message {
    return MessageBuilder().run {
        setContent(content)
        setEmbed(embed)
        setNonce(nonce)
        setTTS(tts)
        allowedMentionTypes?.let { setAllowedMentions(allowedMentionTypes) }
        mentionUsers?.forEach { mentionUsers(it) }
        mentionRoles?.forEach { mentionRoles(it) }
        InlineMessage(this).also(builder)
        build()
    }
}

fun Embed(
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
    builder: InlineEmbed.() -> Unit = {}
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
        InlineEmbed(this).also(builder)
        build()
    }
}

class InlineMessage(val builder: MessageBuilder) {
    var content: String? = null
        set(value) {
            builder.setContent(value)
            field = value
        }

    var embed: MessageEmbed? = null
        set(value) {
            builder.setEmbed(embed)
            field = value
        }

    var nonce: String? = null
        set(value) {
            builder.setNonce(nonce)
            field = value
        }

    var tts: Boolean = false
        set(value) {
            builder.setTTS(value)
            field = value
        }

    var allowedMentionTypes = MessageAction.getDefaultMentions()
        set(value) {
            builder.setAllowedMentions(value)
            field = value
        }

    inline fun mentions(build: InlineMentions.() -> Unit) {
        val mentions = InlineMentions().also(build)
        mentions.users.forEach { builder.mentionUsers(it) }
        mentions.roles.forEach { builder.mentionRoles(it) }
    }

    class InlineMentions {
        val users = mutableListOf<Long>()
        val roles = mutableListOf<Long>()

        fun user(user: User) {
            users.add(user.idLong)
        }
        fun user(id: String) {
            users.add(id.toLong())
        }
        fun user(id: Long) {
            users.add(id)
        }

        fun role(role: Role) {
            roles.add(role.idLong)
        }
        fun role(id: String) {
            roles.add(id.toLong())
        }
        fun role(id: Long) {
            roles.add(id)
        }
    }
}

class InlineEmbed(val builder: EmbedBuilder) {
    var description: String? = null
        set(value) {
            builder.setDescription(value)
            field = value
        }

    var title: String? = null
        set(value) {
            builder.setTitle(value, url)
            field = value
        }

    var url: String? = null
        set(value) {
            builder.setTitle(title, value)
            field = value
        }

    var color: Int? = null
        set(value) {
            builder.setColor(value ?: Role.DEFAULT_COLOR_RAW)
            field = value
        }

    var timestamp: TemporalAccessor? = null
        set(value) {
            builder.setTimestamp(value)
            field = value
        }


    var image: String? = null
        set(value) {
            builder.setImage(value)
            field = value
        }

    var thumbnail: String? = null
        set(value) {
            builder.setThumbnail(value)
            field = value
        }

    inline fun footer(build: InlineFooter.() -> Unit) {
        val footer = InlineFooter().also(build)
        this.builder.setFooter(footer.name, footer.iconUrl)
    }

    inline fun author(build: InlineAuthor.() -> Unit) {
        val author = InlineAuthor().also(build)
        builder.setAuthor(author.name, author.url, author.iconUrl)
    }

    inline fun field(build: InlineField.() -> Unit) {
        val field = InlineField().also(build)
        builder.addField(field.name, field.value, field.inline)
    }


    data class InlineFooter(
        var name: String = "",
        var iconUrl: String? = null
    )

    data class InlineAuthor(
        var name: String? = null,
        var iconUrl: String? = null,
        var url: String? = null
    )

    data class InlineField(
        var name: String = EmbedBuilder.ZERO_WIDTH_SPACE,
        var value: String = EmbedBuilder.ZERO_WIDTH_SPACE,
        var inline: Boolean = true
    )
}