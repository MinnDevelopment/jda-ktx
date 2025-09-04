package dev.minn.jda.ktx.interactions.components

import net.dv8tion.jda.api.components.mediagallery.MediaGalleryItem
import net.dv8tion.jda.api.utils.FileUpload

@InlineComponentDSL
class InlineMediaGalleryItem(
    private var item: MediaGalleryItem,
) {

    /** Known as an "alternative text", must not exceed [MAX_DESCRIPTION_LENGTH][MediaGalleryItem.MAX_DESCRIPTION_LENGTH] characters */
    var description: String?
        get() = item.description
        set(value) {
            item = item.withDescription(value)
        }

    /** Hides the item until the user clicks on it */
    var spoiler: Boolean
        get() = item.isSpoiler
        set(value) {
            item = item.withSpoiler(value)
        }

    fun build(): MediaGalleryItem = item
}

/**
 * See [MediaGalleryItem.fromUrl].
 *
 * @param url         The URL of the image to display
 * @param description Known as an "alternative text", must not exceed [MAX_DESCRIPTION_LENGTH][MediaGalleryItem.MAX_DESCRIPTION_LENGTH] characters
 * @param spoiler     Hides the item until the user clicks on it
 * @param block       Lambda allowing further configuration
 */
inline fun MediaGalleryItem(url: String, description: String? = null, spoiler: Boolean = false, block: InlineMediaGalleryItem.() -> Unit = {}): MediaGalleryItem {
    return InlineMediaGalleryItem(MediaGalleryItem.fromUrl(url))
        .apply {
            if (description != null)
                this.description = description
            if (spoiler)
                this.spoiler = true
            block()
        }
        .build()
}

/**
 * See [MediaGalleryItem.fromFile].
 *
 * @param file        The image to display
 * @param description Known as an "alternative text", must not exceed [MAX_DESCRIPTION_LENGTH][MediaGalleryItem.MAX_DESCRIPTION_LENGTH] characters
 * @param spoiler     Hides the item until the user clicks on it
 * @param block       Lambda allowing further configuration
 */
inline fun MediaGalleryItem(file: FileUpload, description: String? = null, spoiler: Boolean = false, block: InlineMediaGalleryItem.() -> Unit = {}): MediaGalleryItem {
    return InlineMediaGalleryItem(MediaGalleryItem.fromFile(file))
        .apply {
            if (description != null)
                this.description = description
            if (spoiler)
                this.spoiler = true
            block()
        }
        .build()
}
