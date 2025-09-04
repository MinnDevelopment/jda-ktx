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

import dev.minn.jda.ktx.interactions.components.*
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.components.MessageTopLevelComponent
import net.dv8tion.jda.api.components.actionrow.ActionRowChildComponent
import net.dv8tion.jda.api.components.container.ContainerChildComponent
import net.dv8tion.jda.api.components.mediagallery.MediaGalleryItem
import net.dv8tion.jda.api.components.section.SectionAccessoryComponent
import net.dv8tion.jda.api.components.section.SectionContentComponent
import net.dv8tion.jda.api.components.separator.Separator
import net.dv8tion.jda.api.entities.Message.MentionType
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.entities.Role
import net.dv8tion.jda.api.entities.UserSnowflake
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
import kotlin.Boolean
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import kotlin.also
import kotlin.apply
import kotlin.let
import kotlin.run

inline fun MessageCreateBuilder(
    content: String = "",
    embeds: Collection<MessageEmbed> = emptyList(),
    files: Collection<FileUpload> = emptyList(),
    components: Collection<MessageTopLevelComponent> = emptyList(),
    useComponentsV2: Boolean = MessageRequest.isDefaultUseComponentsV2(),
    tts: Boolean = false,
    mentions: Mentions = Mentions.default(),
    builder: InlineMessage<MessageCreateData>.() -> Unit = {}
) = MessageCreateBuilder().run {
    setTTS(tts)
    mentions.apply(this)
    useComponentsV2(useComponentsV2)

    InlineMessage(this).apply {
        this.content = content
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
    components: Collection<MessageTopLevelComponent> = emptyList(),
    useComponentsV2: Boolean = MessageRequest.isDefaultUseComponentsV2(),
    tts: Boolean = false,
    mentions: Mentions = Mentions.default(),
    builder: InlineMessage<MessageCreateData>.() -> Unit = {}
) = MessageCreateBuilder(
    content,
    embeds,
    files,
    components,
    useComponentsV2,
    tts,
    mentions,
    builder
).build()

inline fun MessageEditBuilder(
    content: String? = null,
    embeds: Collection<MessageEmbed>? = null,
    files: Collection<AttachedFile>? = null,
    components: Collection<MessageTopLevelComponent>? = null,
    useComponentsV2: Boolean = MessageRequest.isDefaultUseComponentsV2(),
    mentions: Mentions? = null,
    replace: Boolean = false,
    builder: InlineMessage<MessageEditData>.() -> Unit = {}
) = MessageEditBuilder().run {
    mentions?.apply(this)
    isReplace = replace
    useComponentsV2(useComponentsV2)
    InlineMessage(this).apply {
        content?.let { this.content = it }
        embeds?.let { this.embeds += it }
        components?.let { this.components += it }
        files?.let { this.files += it }
        this.builder()
    }
}

inline fun MessageEdit(
    content: String? = null,
    embeds: Collection<MessageEmbed>? = null,
    files: Collection<AttachedFile>? = null,
    components: Collection<MessageTopLevelComponent>? = null,
    useComponentsV2: Boolean = MessageRequest.isDefaultUseComponentsV2(),
    mentions: Mentions? = null,
    replace: Boolean = false,
    builder: InlineMessage<MessageEditData>.() -> Unit = {}
) = MessageEditBuilder(
    content,
    embeds,
    files,
    components,
    useComponentsV2,
    mentions,
    replace,
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
    internal val configuredComponents = mutableListOf<MessageTopLevelComponent>()
    internal val configuredFiles = mutableListOf<AttachedFile>()
    internal var set = 0

    fun build() = builder.apply {
        if (set and SetFlags.EMBEDS != 0) {
            setEmbeds(configuredEmbeds)
        }
        if (set and SetFlags.COMPONENTS != 0) {
            setComponents(configuredComponents)
        }

        if (set and SetFlags.FILES != 0) {
            if (this is MessageEditBuilder)
                setAttachments(configuredFiles)
            else setFiles(configuredFiles.mapNotNull { it as? FileUpload })
        }
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

    val components = MessageComponentAccumulator(this.configuredComponents, this)

    var useComponentsV2: Boolean
        set(value) { builder.useComponentsV2(value) }
        get() = builder.isUsingComponentsV2

    /**
     * See [ActionRow][net.dv8tion.jda.api.components.actionrow.ActionRow].
     *
     * @param components Components of this row
     * @param uniqueId   Unique identifier of this component
     * @param block      Lambda allowing further configuration
     *
     * @see ActionRowChildComponent
     */
    inline fun actionRow(
        vararg components: ActionRowChildComponent,
        uniqueId: Int = -1,
        block: InlineActionRow.() -> Unit = {},
    ) {
        this.components += ActionRow(uniqueId) {
            this.components += components
            block()
        }
    }

    /**
     * See [ActionRow][net.dv8tion.jda.api.components.actionrow.ActionRow].
     *
     * @param components Components of this row
     * @param uniqueId   Unique identifier of this component
     * @param block      Lambda allowing further configuration
     *
     * @see ActionRowChildComponent
     */
    inline fun actionRow(
        components: Collection<ActionRowChildComponent> = emptyList(),
        uniqueId: Int = -1,
        block: InlineActionRow.() -> Unit = {},
    ) {
        this.components += ActionRow(uniqueId) {
            this.components += components
            block()
        }
    }

    /**
     * See [Section][net.dv8tion.jda.api.components.section.Section].
     *
     * This requires [Components V2][net.dv8tion.jda.api.utils.messages.MessageRequest.useComponentsV2] to be enabled.
     *
     * @param accessory  The accessory of this section
     * @param components The components of this section
     * @param uniqueId   Unique identifier of this component
     * @param block      Lambda allowing further configuration
     *
     * @see SectionContentComponent
     * @see SectionAccessoryComponent
     */
    inline fun section(
        accessory: SectionAccessoryComponent? = null,
        vararg components: SectionContentComponent,
        uniqueId: Int = -1,
        block: InlineSection.() -> Unit = {},
    ) {
        this.components += Section(accessory, uniqueId) {
            this.components += components
            block()
        }
    }

    /**
     * See [Section][net.dv8tion.jda.api.components.section.Section].
     *
     * This requires [Components V2][net.dv8tion.jda.api.utils.messages.MessageRequest.useComponentsV2] to be enabled.
     *
     * @param accessory  The accessory of this section
     * @param components The components of this section
     * @param uniqueId   Unique identifier of this component
     * @param block      Lambda allowing further configuration
     *
     * @see SectionContentComponent
     * @see SectionAccessoryComponent
     */
    inline fun section(
        accessory: SectionAccessoryComponent? = null,
        components: Collection<SectionContentComponent> = emptyList(),
        uniqueId: Int = -1,
        block: InlineSection.() -> Unit = {},
    ) {
        this.components += Section(accessory, uniqueId) {
            this.components += components
            block()
        }
    }

    /**
     * See [TextDisplay][net.dv8tion.jda.api.components.textdisplay.TextDisplay].
     *
     * This requires [Components V2][net.dv8tion.jda.api.utils.messages.MessageRequest.useComponentsV2] to be enabled.
     *
     * @param content  The content displayed by this component
     * @param uniqueId Unique identifier of this component
     * @param block    Lambda allowing further configuration
     */
    inline fun text(
        content: String? = null,
        uniqueId: Int = -1,
        block: InlineTextDisplay.() -> Unit = {},
    ) {
        this.components += TextDisplay(content, uniqueId, block)
    }

    /**
     * See [MediaGallery][net.dv8tion.jda.api.components.mediagallery.MediaGallery].
     *
     * This requires [Components V2][net.dv8tion.jda.api.utils.messages.MessageRequest.useComponentsV2] to be enabled.
     *
     * @param items    Items of this media gallery
     * @param uniqueId Unique identifier of this component
     * @param block    Lambda allowing further configuration
     */
    inline fun mediaGallery(
        vararg items: MediaGalleryItem,
        uniqueId: Int = -1,
        block: InlineMediaGallery.() -> Unit = {},
    ) {
        this.components += MediaGallery(uniqueId) {
            this.items += items
            block()
        }
    }

    /**
     * See [MediaGallery][net.dv8tion.jda.api.components.mediagallery.MediaGallery].
     *
     * This requires [Components V2][net.dv8tion.jda.api.utils.messages.MessageRequest.useComponentsV2] to be enabled.
     *
     * @param items    Items of this media gallery
     * @param uniqueId Unique identifier of this component
     * @param block    Lambda allowing further configuration
     */
    inline fun mediaGallery(
        items: Collection<MediaGalleryItem> = emptyList(),
        uniqueId: Int = -1,
        block: InlineMediaGallery.() -> Unit = {},
    ) {
        this.components += MediaGallery(uniqueId) {
            this.items += items
            block()
        }
    }

    /**
     * See [Separator][net.dv8tion.jda.api.components.separator.Separator].
     *
     * This requires [Components V2][net.dv8tion.jda.api.utils.messages.MessageRequest.useComponentsV2] to be enabled.
     *
     * @param uniqueId  Unique identifier of this component
     * @param isDivider `true` if the separator should be visible
     * @param spacing   The amount of spacing this separator should provide
     * @param block     Lambda allowing further configuration
     */
    inline fun separator(
        uniqueId: Int = -1,
        isDivider: Boolean = true,
        spacing: Separator.Spacing = Separator.Spacing.SMALL,
        block: InlineSeparator.() -> Unit = {},
    ) {
        this.components += Separator(uniqueId, isDivider, spacing, block)
    }

    /**
     * See [FileDisplay.fromFile][net.dv8tion.jda.api.components.filedisplay.FileDisplay.fromFile].
     *
     * This requires [Components V2][net.dv8tion.jda.api.utils.messages.MessageRequest.useComponentsV2] to be enabled.
     *
     * @param file     The file to attach
     * @param uniqueId Unique identifier of this component
     * @param spoiler  Hides the file until the user clicks on it
     * @param block    Lambda allowing further configuration
     */
    fun fileDisplay(
        file: FileUpload,
        uniqueId: Int = -1,
        spoiler: Boolean = false,
        block: InlineFileDisplay.() -> Unit = {},
    ) {
        this.components += FileDisplay(file, uniqueId, spoiler, block)
    }

    /**
     * See [FileDisplay.fromFileName][net.dv8tion.jda.api.components.filedisplay.FileDisplay.fromFileName].
     *
     * This requires [Components V2][net.dv8tion.jda.api.utils.messages.MessageRequest.useComponentsV2] to be enabled.
     *
     * @param fileName Name of the file, you later have to add the file data with a matching name
     * @param uniqueId Unique identifier of this component
     * @param spoiler  Hides the file until the user clicks on it
     * @param block    Lambda allowing further configuration
     */
    fun fileDisplay(
        fileName: String,
        uniqueId: Int = -1,
        spoiler: Boolean = false,
        block: InlineFileDisplay.() -> Unit = {},
    ) {
        this.components += FileDisplay(fileName, uniqueId, spoiler, block)
    }

    /**
     * See [Container][net.dv8tion.jda.api.components.container.Container].
     *
     * This requires [Components V2][net.dv8tion.jda.api.utils.messages.MessageRequest.useComponentsV2] to be enabled.
     *
     * @param components  The components of this container
     * @param uniqueId    Unique identifier of this component
     * @param accentColor Color of the container's left side, you can use [rgb], [hsb] or [hex] for it
     * @param spoiler     Hides the file until the user clicks on it
     * @param block       Lambda allowing further configuration
     *
     * @see ContainerChildComponent
     */
    inline fun container(
        vararg components: ContainerChildComponent,
        uniqueId: Int = -1,
        accentColor: Int? = null,
        spoiler: Boolean = false,
        block: InlineContainer.() -> Unit = {},
    ) {
        this.components += Container(uniqueId, accentColor, spoiler) {
            this.components += components
            block()
        }
    }

    /**
     * See [Container][net.dv8tion.jda.api.components.container.Container].
     *
     * This requires [Components V2][net.dv8tion.jda.api.utils.messages.MessageRequest.useComponentsV2] to be enabled.
     *
     * @param components  The components of this container
     * @param uniqueId    Unique identifier of this component
     * @param accentColor Color of the container's left side, you can use [rgb], [hsb] or [hex] for it
     * @param spoiler     Hides the file until the user clicks on it
     * @param block       Lambda allowing further configuration
     *
     * @see ContainerChildComponent
     */
    inline fun container(
        components: Collection<ContainerChildComponent> = emptyList(),
        uniqueId: Int = -1,
        accentColor: Int? = null,
        spoiler: Boolean = false,
        block: InlineContainer.() -> Unit = {},
    ) {
        this.components += Container(uniqueId, accentColor, spoiler) {
            this.components += components
            block()
        }
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

class MessageComponentAccumulator(private val config: MutableList<MessageTopLevelComponent>, private val builder: InlineMessage<*>) {
    operator fun plusAssign(components: Collection<MessageTopLevelComponent>) {
        builder.let { it.set = it.set or SetFlags.COMPONENTS }
        config += components
    }

    operator fun plusAssign(component: MessageTopLevelComponent) {
        builder.let { it.set = it.set or SetFlags.COMPONENTS }
        config += component
    }

    operator fun minusAssign(components: Collection<MessageTopLevelComponent>) {
        builder.let { it.set = it.set or SetFlags.COMPONENTS }
        config -= components.toSet()
    }

    operator fun minusAssign(component: MessageTopLevelComponent) {
        builder.let { it.set = it.set or SetFlags.COMPONENTS }
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
