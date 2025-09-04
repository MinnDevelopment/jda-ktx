package dev.minn.jda.ktx.interactions.components

import net.dv8tion.jda.api.components.separator.Separator
import net.dv8tion.jda.api.components.separator.Separator.Spacing

// Use default values of factory function
private val DUMMY_SEPARATOR = Separator.create(true, Spacing.SMALL)

class InlineSeparator : InlineComponent {

    private var separator = DUMMY_SEPARATOR

    override var uniqueId: Int
        get() = separator.uniqueId
        set(value) {
            separator = separator.withUniqueId(value)
        }

    /** `true` if the separator should be visible */
    var isDivider: Boolean
        get() = separator.isDivider
        set(value) {
            separator = separator.withDivider(value)
        }

    /** The amount of spacing this separator should provide */
    var spacing: Spacing
        get() = separator.spacing
        set(value) {
            separator = separator.withSpacing(value)
        }

    fun build(): Separator = separator
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
inline fun Separator(uniqueId: Int = -1, isDivider: Boolean = true, spacing: Spacing = Spacing.SMALL, block: InlineSeparator.() -> Unit = {}): Separator =
    InlineSeparator()
        .apply {
            if (uniqueId != -1)
                this.uniqueId = uniqueId
            if (!isDivider)
                this.isDivider = false
            if (spacing != Spacing.SMALL)
                this.spacing = spacing
            block()
        }
        .build()
