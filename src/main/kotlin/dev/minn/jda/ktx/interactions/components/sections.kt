package dev.minn.jda.ktx.interactions.components

import net.dv8tion.jda.api.components.section.Section
import net.dv8tion.jda.api.components.section.SectionAccessoryComponent
import net.dv8tion.jda.api.components.section.SectionContentComponent

class InlineSection(
    /** Unique identifier of this component */
    var uniqueId: Int?,
    /** The accessory of this section */
    var accessory: SectionAccessoryComponent,
) : InlineComponentWithChildren<SectionContentComponent>() {

    fun build(): Section {
        var section = Section.of(accessory, components)
        if (uniqueId != null)
            section = section.withUniqueId(uniqueId!!)
        return section
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
inline fun Section(accessory: SectionAccessoryComponent, uniqueId: Int? = null, block: InlineSection.() -> Unit): Section =
    InlineSection(uniqueId, accessory).apply(block).build()
