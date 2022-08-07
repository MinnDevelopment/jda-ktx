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

import dev.minn.jda.ktx.interactions.components.row
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Message.MentionType
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.entities.Role
import net.dv8tion.jda.api.entities.UserSnowflake
import net.dv8tion.jda.api.interactions.components.ItemComponent
import net.dv8tion.jda.api.interactions.components.LayoutComponent
import net.dv8tion.jda.api.utils.AttachedFile
import net.dv8tion.jda.api.utils.FileUpload
import net.dv8tion.jda.api.utils.messages.AbstractMessageBuilder
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder
import net.dv8tion.jda.api.utils.messages.MessageCreateData
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder
import net.dv8tion.jda.api.utils.messages.MessageEditData
import net.dv8tion.jda.api.utils.messages.MessageRequest
import java.time.temporal.TemporalAccessor
import java.util.*


inline fun MessageCreateBuilder(
    content: String = "",
    embeds: Collection<MessageEmbed> = emptyList(),
    files: Collection<FileUpload> = emptyList(),
    components: Collection<LayoutComponent> = emptyList(),
    tts: Boolean = false,
    mentions: Mentions = Mentions.default(),
    builder: InlineMessage<MessageCreateData>.() -> Unit = {}
) = MessageCreateBuilder().run {
    setContent(content)
    setTTS(tts)
    mentions.apply(this)

    InlineMessage(this).apply {
        this.embeds += embeds
        this.components += components
        this.files += files
        this.builder()
    }
}

inline fun MessageCreate(
    content: String = "",
    embeds: Collection<MessageEmbed> = emptyList(),
    files: Collection<FileUpload> = emptyList(),
    components: Collection<LayoutComponent> = emptyList(),
    tts: Boolean = false,
    mentions: Mentions = Mentions.default(),
    builder: InlineMessage<MessageCreateData>.() -> Unit = {}
) = MessageCreateBuilder(
    content,
    embeds,
    files,
    components,
    tts,
    mentions,
    builder
).build()

inline fun MessageEditBuilder(
    content: String? = null,
    embeds: Collection<MessageEmbed>? = null,
    files: Collection<AttachedFile>? = null,
    components: Collection<LayoutComponent>? = null,
    mentions: Mentions? = null,
    builder: InlineMessage<MessageEditData>.() -> Unit = {}
) = MessageEditBuilder().run {
    mentions?.apply(this)
    InlineMessage(this).apply {
        this.content = content
        embeds?.let { this.embeds += it }
        components?.let { this.components += it }
        files?.let { this.files += it }
        this.builder()
    }
}

inline fun MessageEdi(
    content: String? = null,
    embeds: Collection<MessageEmbed>? = null,
    files: Collection<AttachedFile>? = null,
    components: Collection<LayoutComponent>? = null,
    mentions: Mentions? = null,
    builder: InlineMessage<MessageEditData>.() -> Unit = {}
) = MessageEditBuilder(
    content,
    embeds,
    files,
    components,
    mentions,
    builder
).build()

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

internal object SetFlags {
    const val EMBEDS     = 1 shl 0
    const val FILES      = 1 shl 1
    const val COMPONENTS = 1 shl 2
}

class InlineMessage<T>(val builder: AbstractMessageBuilder<T, *>) {
    internal val configuredEmbeds = mutableListOf<MessageEmbed>()
    internal val configuredComponents = mutableListOf<LayoutComponent>()
    internal val configuredFiles = mutableListOf<AttachedFile>()
    internal var set = 0

    fun build() = builder.apply {
        if (set and SetFlags.EMBEDS != 0) {
            setEmbeds(configuredEmbeds)
        }
        if (set and SetFlags.COMPONENTS != 0) {
            setComponents(configuredComponents)
        }

        if (this is MessageEditBuilder && set and SetFlags.FILES != 0)
            setAttachments(configuredFiles)
        else
            setFiles(configuredFiles.mapNotNull { it as? FileUpload })
    }.build()

    var content: String? = null
        set(value) {
            builder.setContent(value)
            field = value
        }

    val files = FileAccumulator(this)

    val embeds = EmbedAccumulator(this)

    inline fun embed(builder: InlineEmbed.() -> Unit) {
        embeds += EmbedBuilder(description = null).apply(builder).build()
    }

    val components = ComponentAccumulator(this.configuredComponents, this)

    fun actionRow(vararg components: ItemComponent) {
        this.components += row(*components)
    }

    fun actionRow(components: Collection<ItemComponent>) {
        this.components += components.row()
    }

    var allowedMentionTypes = MessageRequest.getDefaultMentions()
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

        fun user(user: UserSnowflake) {
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

class EmbedAccumulator(private val builder: InlineMessage<*>) {
    operator fun plusAssign(embeds: Collection<MessageEmbed>) {
        builder.set = builder.set or SetFlags.EMBEDS
        builder.configuredEmbeds += embeds
    }

    operator fun plusAssign(embed: MessageEmbed) {
        builder.set = builder.set or SetFlags.EMBEDS
        builder.configuredEmbeds += embed
    }

    operator fun minusAssign(embeds: Collection<MessageEmbed>) {
        builder.set = builder.set or SetFlags.EMBEDS
        builder.configuredEmbeds -= embeds.toSet()
    }

    operator fun minusAssign(embed: MessageEmbed) {
        builder.set = builder.set or SetFlags.EMBEDS
        builder.configuredEmbeds -= embed
    }
}

class ComponentAccumulator(private val config: MutableList<LayoutComponent>, private val builder: InlineMessage<*>? = null) {
    operator fun plusAssign(components: Collection<LayoutComponent>) {
        builder?.let { it.set = it.set or SetFlags.COMPONENTS }
        config += components
    }

    operator fun plusAssign(component: LayoutComponent) {
        builder?.let { it.set = it.set or SetFlags.COMPONENTS }
        config += component
    }

    operator fun minusAssign(components: Collection<LayoutComponent>) {
        builder?.let { it.set = it.set or SetFlags.COMPONENTS }
        config -= components.toSet()
    }

    operator fun minusAssign(component: LayoutComponent) {
        builder?.let { it.set = it.set or SetFlags.COMPONENTS }
        config -= component
    }
}

class FileAccumulator(private val builder: InlineMessage<*>) {
    operator fun plusAssign(files: Collection<AttachedFile>) {
        builder.set = builder.set or SetFlags.FILES
        builder.configuredFiles += files
    }

    operator fun plusAssign(file: AttachedFile) {
        builder.set = builder.set or SetFlags.FILES
        builder.configuredFiles += file
    }

    operator fun minusAssign(files: Collection<AttachedFile>) {
        builder.set = builder.set or SetFlags.FILES
        builder.configuredFiles -= files.toSet()
    }

    operator fun minusAssign(file: AttachedFile) {
        builder.set = builder.set or SetFlags.FILES
        builder.configuredFiles -= file
    }
}


class MentionConfig internal constructor(
    val any: Boolean,
    val list: List<Long>,
    val type: MentionType
) {
    companion object {
        val USERS = MentionConfig(true, emptyList(), MentionType.USER)
        val ROLES = MentionConfig(true, emptyList(), MentionType.ROLE)
        val EVERYONE = MentionConfig(true, emptyList(), MentionType.EVERYONE)
        val HERE = MentionConfig(true, emptyList(), MentionType.HERE)

        fun users(list: Collection<Long>) = MentionConfig(false, list.toList(), MentionType.USER)
        fun roles(list: Collection<Long>) = MentionConfig(false, list.toList(), MentionType.ROLE)
    }
}

data class Mentions(
    var users: MentionConfig,
    var roles: MentionConfig,
    var everyone: Boolean,
    var here: Boolean
) {
    fun apply(request: MessageRequest<*>) {
        val types = EnumSet.noneOf(MentionType::class.java)
        if (everyone) types.add(MentionType.EVERYONE)
        if (here) types.add(MentionType.HERE)
        if (users.any) types.add(MentionType.USER)
        if (roles.any) types.add(MentionType.ROLE)

        request.setAllowedMentions(types)
        if (!users.any)
            users.list.forEach(request::mentionUsers)
        if (!roles.any)
            roles.list.forEach(request::mentionRoles)
    }

    operator fun plusAssign(config: MentionConfig) {
        when (config.type) {
            MentionType.EVERYONE -> everyone = config.any
            MentionType.HERE -> here = config.any
            MentionType.USER -> users = config
            MentionType.ROLE -> roles = config
            else -> {}
        }
    }

    companion object {
        fun default(): Mentions {
            val defaultTypes = MessageRequest.getDefaultMentions()

            return Mentions(
                MentionConfig(MentionType.USER in defaultTypes, emptyList(), MentionType.USER),
                MentionConfig(MentionType.ROLE in defaultTypes, emptyList(), MentionType.ROLE),
                MentionType.EVERYONE in defaultTypes,
                MentionType.HERE in defaultTypes
            )
        }

        fun of(vararg configs: MentionConfig): Mentions {
            val allowedMentions = default()

            for (config in configs)
                allowedMentions += config

            return allowedMentions
        }
    }
}