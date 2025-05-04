package dev.minn.jda.ktx.interactions.components

import net.dv8tion.jda.api.components.filedisplay.FileDisplay
import net.dv8tion.jda.api.utils.FileUpload

class InlineFileDisplay(
    private val fileDisplay: FileDisplay,
    /** Unique identifier of this component */
    var uniqueId: Int?,
    /** Hides the file until the user clicks on it */
    var spoiler: Boolean,
) : InlineComponent {

    fun build(): FileDisplay {
        var fileDisplay = fileDisplay
            .withSpoiler(spoiler)
        if (uniqueId != null)
            fileDisplay = fileDisplay.withUniqueId(uniqueId!!)
        return fileDisplay
    }
}

/**
 * Component displaying a file, you can mark it as a spoiler.
 *
 * **Note:** Audio files and text files cannot be _previewed_.
 *
 * This requires [Components V2][net.dv8tion.jda.api.utils.messages.MessageRequest.useComponentsV2] to be enabled.
 *
 * @param uniqueId    Unique identifier of this component
 * @param spoiler     Hides the file until the user clicks on it
 * @param block       Lambda allowing further configuration
 */
fun FileDisplay(file: FileUpload, uniqueId: Int? = null, spoiler: Boolean = false, block: InlineFileDisplay.() -> Unit = {}): FileDisplay =
    InlineFileDisplay(FileDisplay.fromFile(file), uniqueId, spoiler).apply(block).build()