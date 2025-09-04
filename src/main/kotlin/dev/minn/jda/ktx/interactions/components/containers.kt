package dev.minn.jda.ktx.interactions.components

import net.dv8tion.jda.api.components.actionrow.ActionRowChildComponent
import net.dv8tion.jda.api.components.container.Container
import net.dv8tion.jda.api.components.container.ContainerChildComponent
import net.dv8tion.jda.api.components.mediagallery.MediaGalleryItem
import net.dv8tion.jda.api.components.section.SectionAccessoryComponent
import net.dv8tion.jda.api.components.section.SectionContentComponent
import net.dv8tion.jda.api.components.separator.Separator
import net.dv8tion.jda.api.components.textdisplay.TextDisplay
import net.dv8tion.jda.api.utils.FileUpload
import java.awt.Color

private val DUMMY_CONTAINER = Container.of(TextDisplay.of("a"))

class InlineContainer : InlineComponent {

    private var container = DUMMY_CONTAINER

    override var uniqueId: Int
        get() = container.uniqueId
        set(value) {
            container = container.withUniqueId(value)
        }

    /** Color of the container's left side, you can use [rgb], [hsb] or [hex] for it */
    var accentColorRaw: Int?
        get() = container.accentColorRaw
        set(value) {
            container = container.withAccentColor(value)
        }

    /** Color of the container's left side, you can use [rgb], [hsb] or [hex] for it */
    var accentColor: Color?
        get() = container.accentColor
        set(value) {
            container = container.withAccentColor(value)
        }

    /** Hides the file until the user clicks on it */
    var spoiler: Boolean
        get() = container.isSpoiler
        set(value) {
            container = container.withSpoiler(value)
        }

    val components = mutableListOf<ContainerChildComponent>()

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

    fun build(): Container {
        return container.withComponents(components)
    }
}

/**
 * See [Container][net.dv8tion.jda.api.components.container.Container].
 *
 * This requires [Components V2][net.dv8tion.jda.api.utils.messages.MessageRequest.useComponentsV2] to be enabled.
 *
 * @param uniqueId    Unique identifier of this component
 * @param accentColor Color of the container's left side, you can use [rgb], [hsb] or [hex] for it
 * @param spoiler     Hides the file until the user clicks on it
 * @param block       Lambda allowing further configuration
 *
 * @see ContainerChildComponent
 */
inline fun Container(uniqueId: Int = -1, accentColor: Int? = null, spoiler: Boolean = false, block: InlineContainer.() -> Unit): Container =
    InlineContainer()
        .apply {
            if (uniqueId != -1)
                this.uniqueId = uniqueId
            if (accentColor != null)
                this.accentColorRaw = accentColor
            if (spoiler)
                this.spoiler = true
            block()
        }
        .build()
