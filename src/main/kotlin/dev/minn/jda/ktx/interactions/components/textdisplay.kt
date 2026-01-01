package dev.minn.jda.ktx.interactions.components

import dev.minn.jda.ktx.interactions.components.utils.checkInit
import net.dv8tion.jda.api.components.textdisplay.TextDisplay

private val DUMMY_TEXT_DISPLAY = TextDisplay.of("a")

class InlineTextDisplay : InlineComponent {

    private var textDisplay = DUMMY_TEXT_DISPLAY

    override var uniqueId: Int
        get() = textDisplay.uniqueId
        set(value) {
            textDisplay = textDisplay.withUniqueId(value)
        }

    private var _content: String? = null
    /** The content displayed by this component */
    var content: String
        get() = _content.checkInit("content")
        set(value) {
            textDisplay = textDisplay.withContent(value)
            _content = value
        }

    val hasContent: Boolean get() = _content != null

    fun build(): TextDisplay {
        content.checkInit()
        return textDisplay
    }
}

/**
 * See [TextDisplay][net.dv8tion.jda.api.components.textdisplay.TextDisplay].
 *
 * This requires [Components V2][net.dv8tion.jda.api.utils.messages.MessageRequest.useComponentsV2] to be enabled.
 *
 * @param content   The content displayed by this component
 * @param uniqueId  Unique identifier of this component
 * @param block     Lambda allowing further configuration
 */
inline fun TextDisplay(content: String? = null, uniqueId: Int = -1, block: InlineTextDisplay.() -> Unit = {}): TextDisplay =
    InlineTextDisplay()
        .apply {
            if (uniqueId != -1)
                this.uniqueId = uniqueId
            if (content != null)
                this.content = content
            block()
        }
        .build()
