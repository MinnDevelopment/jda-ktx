package dev.minn.jda.ktx.interactions.components

import net.dv8tion.jda.api.components.textdisplay.TextDisplay

class InlineTextDisplay(
    /** The content displayed by this component */
    var content: String,
    /** Unique identifier of this component */
    var uniqueId: Int?,
) : InlineComponent {

    fun build(): TextDisplay {
        var textDisplay = TextDisplay.of(content)
        if (uniqueId != null)
            textDisplay = textDisplay.withUniqueId(uniqueId!!)
        return textDisplay
    }
}

/**
 * A component to display text, supports Markdown.
 *
 * This component has no content length limit, however,
 * you are still limited to the [MAX_CONTENT_LENGTH_COMPONENT_V2][net.dv8tion.jda.api.entities.Message.MAX_CONTENT_LENGTH_COMPONENT_V2] total characters,
 * as imposed by [MessageRequest.useComponentsV2][net.dv8tion.jda.api.utils.messages.MessageRequest.useComponentsV2].
 *
 * This requires [Components V2][net.dv8tion.jda.api.utils.messages.MessageRequest.useComponentsV2] to be enabled.
 *
 * @param content   The content displayed by this component
 * @param uniqueId  Unique identifier of this component
 * @param block     Lambda allowing further configuration
 */
inline fun TextDisplay(content: String, uniqueId: Int? = null, block: InlineTextDisplay.() -> Unit = {}): TextDisplay =
    InlineTextDisplay(content, uniqueId).apply(block).build()