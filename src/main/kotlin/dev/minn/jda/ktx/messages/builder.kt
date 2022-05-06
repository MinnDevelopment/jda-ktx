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

package dev.minn.jda.ktx.messages

import dev.minn.jda.ktx.interactions.row
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.MessageBuilder
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.entities.Role
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.interactions.components.ActionRow
import net.dv8tion.jda.api.interactions.components.ItemComponent
import net.dv8tion.jda.api.interactions.components.LayoutComponent
import net.dv8tion.jda.api.requests.restaction.MessageAction
import java.time.temporal.TemporalAccessor

inline fun Message(
    content: String? = null,
    embed: MessageEmbed? = null,
    embeds: Embeds? = null,
    components: Components? = null,
    nonce: String? = null,
    tts: Boolean = false,
    allowedMentionTypes: Collection<Message.MentionType>? = null,
    mentionUsers: Collection<Long>? = null,
    mentionRoles: Collection<Long>? = null,
    builder: InlineMessage.() -> Unit = {},
): Message {
    return MessageBuilder(content, embed, embeds, components, nonce, tts, allowedMentionTypes, mentionUsers, mentionRoles, builder).build()
}

inline fun Embed(
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
    builder: InlineEmbed.() -> Unit = {},
): MessageEmbed {
    return EmbedBuilder(description, title, url, color, footerText, footerIcon,
        authorName, authorIcon, authorUrl, timestamp, image, thumbnail, fields, builder
    ).build()
}

inline fun MessageBuilder(
    content: String? = null,
    embed: MessageEmbed? = null,
    embeds: Embeds? = null,
    components: Components? = null,
    nonce: String? = null,
    tts: Boolean = false,
    allowedMentionTypes: Collection<Message.MentionType>? = null,
    mentionUsers: Collection<Long>? = null,
    mentionRoles: Collection<Long>? = null,
    builder: InlineMessage.() -> Unit = {}
): InlineMessage {
    return MessageBuilder().run {
        setContent(content)
        setNonce(nonce)
        setTTS(tts)
        allowedMentionTypes?.let { setAllowedMentions(it) }
        mentionUsers?.forEach { mentionUsers(it) }
        mentionRoles?.forEach { mentionRoles(it) }

        InlineMessage(this).apply {
            this.embeds += allOf(embed, embeds) ?: emptyList()
            this.components += components ?: emptyList()
            this.builder()
        }
    }
}

inline fun EmbedBuilder(
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
): InlineEmbed {
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
        InlineEmbed(this).apply(builder)
    }
}

class InlineMessage(val builder: MessageBuilder) {
    constructor(message: Message) : this(MessageBuilder(message))

    internal val configuredEmbeds = mutableListOf<MessageEmbed>()
    internal val configuredComponents = mutableListOf<LayoutComponent>()

    fun build() = builder
        .setEmbeds(configuredEmbeds)
        .setActionRows(configuredComponents.mapNotNull { it as? ActionRow })
        .build()

    var content: String? = null
        set(value) {
            builder.setContent(value)
            field = value
        }

    @Deprecated("You should use the embeds property instead, which accepts a collection of embeds", ReplaceWith("embeds"), DeprecationLevel.ERROR)
    var embed: MessageEmbed? = null
        set(value) {
            configuredEmbeds.clear()
            value?.let(configuredEmbeds::add)
            field = value
        }

    val embeds = EmbedAccumulator(this)

    inline fun embed(builder: InlineEmbed.() -> Unit) {
        embeds += EmbedBuilder(description = null).apply(builder).build()
    }

    val components = ComponentAccumulator(this.configuredComponents)

    fun actionRow(vararg components: ItemComponent) {
        this.components += row(*components)
    }

    fun actionRow(components: Collection<ItemComponent>) {
        this.components += components.row()
    }

    var nonce: String? = null
        set(value) {
            builder.setNonce(value)
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
    constructor(embed: MessageEmbed) : this(EmbedBuilder(embed))

    fun build() = builder.build()

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

    inline fun footer(name: String = "", iconUrl: String? = null, build: InlineFooter.() -> Unit = {}) {
        val footer = InlineFooter(name, iconUrl).apply(build)
        this.builder.setFooter(footer.name, footer.iconUrl)
    }

    inline fun author(name: String? = null, url: String? = null, iconUrl: String? = null, build: InlineAuthor.() -> Unit = {}) {
        val author = InlineAuthor(name, iconUrl, url).apply(build)
        builder.setAuthor(author.name, author.url, author.iconUrl)
    }

    inline fun field(
        name: String = EmbedBuilder.ZERO_WIDTH_SPACE,
        value: String = EmbedBuilder.ZERO_WIDTH_SPACE,
        inline: Boolean = true,
        build: InlineField.() -> Unit = {}
    ) {
        val field = InlineField(name, value, inline).apply(build)
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

class EmbedAccumulator(private val builder: InlineMessage) {
    operator fun plusAssign(embeds: Collection<MessageEmbed>) {
        builder.configuredEmbeds += embeds
    }

    operator fun plusAssign(embed: MessageEmbed) {
        builder.configuredEmbeds += embed
    }

    operator fun minusAssign(embeds: Collection<MessageEmbed>) {
        builder.configuredEmbeds -= embeds.toSet()
    }

    operator fun minusAssign(embed: MessageEmbed) {
        builder.configuredEmbeds -= embed
    }
}

class ComponentAccumulator(private val config: MutableList<LayoutComponent>) {
    operator fun plusAssign(components: Collection<LayoutComponent>) {
        config += components
    }

    operator fun plusAssign(component: LayoutComponent) {
        config += component
    }

    operator fun minusAssign(components: Collection<LayoutComponent>) {
        config -= components.toSet()
    }

    operator fun minusAssign(component: LayoutComponent) {
        config -= component
    }
}