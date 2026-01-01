package dev.minn.jda.ktx.interactions.components

import net.dv8tion.jda.api.components.thumbnail.Thumbnail
import net.dv8tion.jda.api.utils.FileUpload

class InlineThumbnail(
    private var thumbnail: Thumbnail,
) : InlineComponent {

    override var uniqueId: Int
        get() = thumbnail.uniqueId
        set(value) {
            thumbnail = thumbnail.withUniqueId(value)
        }

    /** Known as an "alternative text", must not exceed [MAX_DESCRIPTION_LENGTH][net.dv8tion.jda.api.components.thumbnail.Thumbnail.MAX_DESCRIPTION_LENGTH] characters */
    var description: String?
        get() = thumbnail.description
        set(value) {
            thumbnail = thumbnail.withDescription(value)
        }

    /** Hides the thumbnail until the user clicks on it */
    var spoiler: Boolean
        get() = thumbnail.isSpoiler
        set(value) {
            thumbnail = thumbnail.withSpoiler(value)
        }

    fun build(): Thumbnail = thumbnail
}

/**
 * See [Thumbnail.fromUrl].
 *
 * This requires [Components V2][net.dv8tion.jda.api.utils.messages.MessageRequest.useComponentsV2] to be enabled.
 *
 * @param url         The URL of the image to display
 * @param uniqueId    Unique identifier of this component
 * @param description Known as an "alternative text", must not exceed [MAX_DESCRIPTION_LENGTH][net.dv8tion.jda.api.components.thumbnail.Thumbnail.MAX_DESCRIPTION_LENGTH] characters
 * @param spoiler     Hides the thumbnail until the user clicks on it
 * @param block       Lambda allowing further configuration
 */
fun Thumbnail(url: String, uniqueId: Int = -1, description: String? = null, spoiler: Boolean = false, block: InlineThumbnail.() -> Unit = {}): Thumbnail =
    InlineThumbnail(Thumbnail.fromUrl(url))
        .apply {
            if (uniqueId != -1)
                this.uniqueId = uniqueId
            if (description != null)
                this.description = description
            if (spoiler)
                this.spoiler = true
            block()
        }
        .build()

/**
 * See [Thumbnail.fromFile].
 *
 * This requires [Components V2][net.dv8tion.jda.api.utils.messages.MessageRequest.useComponentsV2] to be enabled.
 *
 * @param file        The image to display
 * @param uniqueId    Unique identifier of this component
 * @param description Known as an "alternative text", must not exceed [MAX_DESCRIPTION_LENGTH][net.dv8tion.jda.api.components.thumbnail.Thumbnail.MAX_DESCRIPTION_LENGTH] characters.
 * @param spoiler     Hides the thumbnail until the user clicks on it
 * @param block       Lambda allowing further configuration
 */
fun Thumbnail(file: FileUpload, uniqueId: Int = -1, description: String? = null, spoiler: Boolean = false, block: InlineThumbnail.() -> Unit = {}): Thumbnail =
    InlineThumbnail(Thumbnail.fromFile(file))
        .apply {
            if (uniqueId != -1)
                this.uniqueId = uniqueId
            if (description != null)
                this.description = description
            if (spoiler)
                this.spoiler = true
            block()
        }
        .build()
