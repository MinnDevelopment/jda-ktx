package dev.minn.jda.ktx.interactions.components

import net.dv8tion.jda.api.components.separator.Separator

class InlineSeparator(
    /** `true` if the separator should be visible */
    var isDivider: Boolean,
    /** The amount of spacing this separator should provide */
    var spacing: Separator.Spacing,
    /** Unique identifier of this component */
    var uniqueId: Int?,
) : InlineComponent {

    fun build(): Separator {
        var separator = Separator.create(isDivider, spacing)
        if (uniqueId != null)
            separator = separator.withUniqueId(uniqueId!!)
        return separator
    }
}

/**
 * A component to separate content vertically, you can change its size and make it invisible.
 *
 * This requires [Components V2][net.dv8tion.jda.api.utils.messages.MessageRequest.useComponentsV2] to be enabled.
 *
 * @param isDivider `true` if the separator should be visible
 * @param spacing   The amount of spacing this separator should provide
 * @param uniqueId  Unique identifier of this component
 * @param block     Lambda allowing further configuration
 */
inline fun Separator(isDivider: Boolean, spacing: Separator.Spacing, uniqueId: Int? = null, block: InlineSeparator.() -> Unit = {}): Separator =
    InlineSeparator(isDivider, spacing, uniqueId).apply(block).build()