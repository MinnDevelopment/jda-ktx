package dev.minn.jda.ktx.interactions.components

import net.dv8tion.jda.api.components.mediagallery.MediaGallery
import net.dv8tion.jda.api.components.mediagallery.MediaGalleryItem
import net.dv8tion.jda.api.utils.FileUpload

private val DUMMY_MEDIA_GALLERY = MediaGallery.of(MediaGalleryItem.fromUrl("https://github.com"))

class InlineMediaGallery : InlineComponent {

    private var mediaGallery = DUMMY_MEDIA_GALLERY

    override var uniqueId: Int
        get() = mediaGallery.uniqueId
        set(value) {
            mediaGallery = mediaGallery.withUniqueId(value)
        }

    val items = mutableListOf<MediaGalleryItem>()

    /**
     * Add an item to this gallery, see [MediaGalleryItem.fromUrl].
     *
     * @param url         The URL of the image to display
     * @param description Known as an "alternative text", must not exceed [MAX_DESCRIPTION_LENGTH][MediaGalleryItem.MAX_DESCRIPTION_LENGTH] characters
     * @param spoiler     Hides the item until the user clicks on it
     * @param block       Lambda allowing further configuration
     */
    inline fun item(url: String, description: String? = null, spoiler: Boolean = false, block: InlineMediaGalleryItem.() -> Unit = {}) {
        items += MediaGalleryItem(url, description, spoiler, block)
    }

    /**
     * Add an item to this gallery, see [MediaGalleryItem.fromFile].
     *
     * @param file        The image to display
     * @param description Known as an "alternative text", must not exceed [MAX_DESCRIPTION_LENGTH][MediaGalleryItem.MAX_DESCRIPTION_LENGTH] characters
     * @param spoiler     Hides the item until the user clicks on it
     * @param block       Lambda allowing further configuration
     */
    inline fun item(file: FileUpload, description: String? = null, spoiler: Boolean = false, block: InlineMediaGalleryItem.() -> Unit = {}) {
        items += MediaGalleryItem(file, description, spoiler, block)
    }

    fun build(): MediaGallery {
        return mediaGallery.withItems(items)
    }
}

/**
 * See [MediaGallery][net.dv8tion.jda.api.components.mediagallery.MediaGallery].
 *
 * This requires [Components V2][net.dv8tion.jda.api.utils.messages.MessageRequest.useComponentsV2] to be enabled.
 *
 * @param uniqueId Unique identifier of this component
 * @param block    Lambda allowing further configuration
 */
inline fun MediaGallery(uniqueId: Int = -1, block: InlineMediaGallery.() -> Unit): MediaGallery {
    return InlineMediaGallery()
        .apply {
            if (uniqueId != -1)
                this.uniqueId = uniqueId
            block()
        }
        .build()
}
