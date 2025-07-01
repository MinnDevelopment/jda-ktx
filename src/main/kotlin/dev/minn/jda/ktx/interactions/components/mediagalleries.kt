package dev.minn.jda.ktx.interactions.components

import net.dv8tion.jda.api.components.mediagallery.MediaGallery
import net.dv8tion.jda.api.components.mediagallery.MediaGalleryItem
import net.dv8tion.jda.api.utils.FileUpload

@InlineComponentDSL
class InlineMediaGalleryItem(
    private val item: MediaGalleryItem,
    /** Known as an "alternative text", must not exceed [MAX_DESCRIPTION_LENGTH][MediaGalleryItem.MAX_DESCRIPTION_LENGTH] characters */
    var description: String? = null,
    /** Hides the item until the user clicks on it */
    var spoiler: Boolean = false,
) {

    fun build(): MediaGalleryItem {
        return item
            .withDescription(description)
            .withSpoiler(spoiler)
    }
}

class InlineMediaGallery(
    /** Unique identifier of this component */
    var uniqueId: Int?,
) : InlineComponentWithChildren<MediaGalleryItem>() {

    /**
     * See [MediaGalleryItem.fromUrl].
     *
     * @param url         The URL of the image to display
     * @param description Known as an "alternative text", must not exceed [MAX_DESCRIPTION_LENGTH][MediaGalleryItem.MAX_DESCRIPTION_LENGTH] characters
     * @param spoiler     Hides the item until the user clicks on it
     * @param block       Lambda allowing further configuration
     */
    fun item(url: String, description: String? = null, spoiler: Boolean = false, block: InlineMediaGalleryItem.() -> Unit = {}): Unit =
        +InlineMediaGalleryItem(MediaGalleryItem.fromUrl(url), description, spoiler).apply(block).build()

    /**
     * See [MediaGalleryItem.fromFile].
     *
     * @param file        The image to display
     * @param description Known as an "alternative text", must not exceed [MAX_DESCRIPTION_LENGTH][MediaGalleryItem.MAX_DESCRIPTION_LENGTH] characters
     * @param spoiler     Hides the item until the user clicks on it
     * @param block       Lambda allowing further configuration
     */
    fun item(file: FileUpload, description: String? = null, spoiler: Boolean = false, block: InlineMediaGalleryItem.() -> Unit = {}): Unit =
        +InlineMediaGalleryItem(MediaGalleryItem.fromFile(file), description, spoiler).apply(block).build()

    fun build(): MediaGallery {
        var gallery = MediaGallery.of(components)
        if (uniqueId != null)
            gallery = gallery.withUniqueId(uniqueId!!)
        return gallery
    }
}

/**
 * See [MediaGallery][net.dv8tion.jda.api.components.mediagallery.MediaGallery].
 *
 * This requires [Components V2][net.dv8tion.jda.api.utils.messages.MessageRequest.useComponentsV2] to be enabled.
 *
 * @param uniqueId    Unique identifier of this component
 * @param block       Lambda allowing further configuration
 */
inline fun MediaGallery(uniqueId: Int? = null, block: InlineMediaGallery.() -> Unit): MediaGallery {
    return InlineMediaGallery(uniqueId).apply(block).build()
}
