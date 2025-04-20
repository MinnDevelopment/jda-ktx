package dev.minn.jda.ktx.interactions.components

import net.dv8tion.jda.api.components.thumbnail.Thumbnail
import net.dv8tion.jda.api.utils.FileUpload

class InlineThumbnail(
    private val thumbnail: Thumbnail,
    /** Unique identifier of this component */
    var uniqueId: Int?,
    /** Known as an "alternative text", must not exceed [MAX_DESCRIPTION_LENGTH][net.dv8tion.jda.api.components.thumbnail.Thumbnail.MAX_DESCRIPTION_LENGTH] characters */
    var description: String?,
    /** Hides the thumbnail until the user clicks on it */
    var spoiler: Boolean,
) {

    fun build(): Thumbnail {
        var thumbnail = thumbnail
            .withSpoiler(spoiler)
            .withDescription(description)
        if (uniqueId != null)
            thumbnail = thumbnail.withUniqueId(uniqueId!!)
        return thumbnail
    }
}

/**
 * Component displaying a thumbnail, you can mark it as a spoiler and set a description.
 *
 * This requires [Components V2][net.dv8tion.jda.api.utils.messages.MessageRequest.useComponentsV2] to be enabled.
 *
 * @param url         The URL of the image to display
 * @param uniqueId    Unique identifier of this component
 * @param description Known as an "alternative text", must not exceed [MAX_DESCRIPTION_LENGTH][net.dv8tion.jda.api.components.thumbnail.Thumbnail.MAX_DESCRIPTION_LENGTH] characters
 * @param spoiler     Hides the thumbnail until the user clicks on it
 * @param block       Lambda allowing further configuration
 */
fun Thumbnail(url: String, uniqueId: Int? = null, description: String? = null, spoiler: Boolean = false, block: InlineThumbnail.() -> Unit = {}): Thumbnail =
    InlineThumbnail(Thumbnail.fromUrl(url), uniqueId, description, spoiler).apply(block).build()

/**
 * Component displaying a thumbnail, you can mark it as a spoiler and set a description.
 *
 * This will automatically add the file when building the message;
 * as such, you do not need to add it manually (with [MessageCreateBuilder.addFiles][net.dv8tion.jda.api.utils.messages.MessageCreateBuilder.addFiles] for example).
 *
 * This requires [Components V2][net.dv8tion.jda.api.utils.messages.MessageRequest.useComponentsV2] to be enabled.
 *
 * @param file        The image to display
 * @param uniqueId    Unique identifier of this component
 * @param description Known as an "alternative text", must not exceed [MAX_DESCRIPTION_LENGTH][net.dv8tion.jda.api.components.thumbnail.Thumbnail.MAX_DESCRIPTION_LENGTH] characters.
 * @param spoiler     Hides the thumbnail until the user clicks on it
 * @param block       Lambda allowing further configuration
 */
fun Thumbnail(file: FileUpload, uniqueId: Int? = null, description: String? = null, spoiler: Boolean = false, block: InlineThumbnail.() -> Unit = {}): Thumbnail =
    InlineThumbnail(Thumbnail.fromFile(file), uniqueId, description, spoiler).apply(block).build()