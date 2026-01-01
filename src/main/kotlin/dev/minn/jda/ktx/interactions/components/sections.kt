package dev.minn.jda.ktx.interactions.components

import dev.minn.jda.ktx.interactions.components.utils.checkInit
import net.dv8tion.jda.api.components.buttons.Button
import net.dv8tion.jda.api.components.section.Section
import net.dv8tion.jda.api.components.section.SectionAccessoryComponent
import net.dv8tion.jda.api.components.section.SectionContentComponent
import net.dv8tion.jda.api.components.textdisplay.TextDisplay

private val DUMMY_SECTION = Section.of(Button.success("id", "label"), TextDisplay.of("a"))

class InlineSection : InlineComponent {

    private var section = DUMMY_SECTION

    override var uniqueId: Int
        get() = section.uniqueId
        set(value) {
            section = section.withUniqueId(value)
        }

    private var _accessory: SectionAccessoryComponent? = null
    /** The accessory of this section */
    var accessory: SectionAccessoryComponent
        get() = _accessory.checkInit("accessory")
        set(value) {
            _accessory = value
            section = section.withAccessory(value)
        }

    val hasAccessory: Boolean get() = _accessory != null

    val components = mutableListOf<SectionContentComponent>()

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

    fun build(): Section {
        accessory.checkInit()
        return section.withContentComponents(components)
    }
}

/**
 * See [Section][net.dv8tion.jda.api.components.section.Section].
 *
 * This requires [Components V2][net.dv8tion.jda.api.utils.messages.MessageRequest.useComponentsV2] to be enabled.
 *
 * @param accessory The accessory of this section
 * @param uniqueId  Unique identifier of this component
 * @param block     Lambda allowing further configuration
 *
 * @see SectionContentComponent
 * @see SectionAccessoryComponent
 */
inline fun Section(accessory: SectionAccessoryComponent? = null, uniqueId: Int = -1, block: InlineSection.() -> Unit): Section =
    InlineSection()
        .apply {
            if (accessory != null)
                this.accessory = accessory
            if (uniqueId != -1)
                this.uniqueId = uniqueId
            block()
        }
        .build()
