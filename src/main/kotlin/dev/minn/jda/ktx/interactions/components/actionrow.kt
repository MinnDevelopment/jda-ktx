package dev.minn.jda.ktx.interactions.components

import net.dv8tion.jda.api.components.actionrow.ActionRow
import net.dv8tion.jda.api.components.actionrow.ActionRowChildComponent

class InlineActionRow(
    /** Unique identifier of this component */
    var uniqueId: Int?,
) : InlineComponentWithChildren<ActionRowChildComponent>() {

    fun build(): ActionRow {
        var row = ActionRow.of(components)
        if (uniqueId != null)
            row = row.withUniqueId(uniqueId!!)
        return row
    }
}

/**
 * See [ActionRow][net.dv8tion.jda.api.components.actionrow.ActionRow].
 *
 * @param uniqueId    Unique identifier of this component
 * @param block       Lambda allowing further configuration
 *
 * @see ActionRowChildComponent
 */
inline fun ActionRow(uniqueId: Int? = null, block: InlineActionRow.() -> Unit): ActionRow =
    InlineActionRow(uniqueId).apply(block).build()
