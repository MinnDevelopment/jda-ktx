package dev.minn.jda.ktx.interactions.components

import net.dv8tion.jda.api.components.container.Container
import net.dv8tion.jda.api.components.container.ContainerChildComponent

class InlineContainer(
    /** Unique identifier of this component */
    var uniqueId: Int?,
    /** Color of the container's left side, you can use [rgb], [hsb] or [hex] for it */
    var accentColor: Int?,
    /** Hides the file until the user clicks on it */
    var spoiler: Boolean,
) : InlineComponentWithChildren<ContainerChildComponent>() {

    fun build(): Container {
        var container = Container.of(components)
            .withSpoiler(spoiler)
            .withAccentColor(accentColor)
        if (uniqueId != null)
            container = container.withUniqueId(uniqueId!!)
        return container
    }
}

/**
 * Component which groups components vertically, you can specify an accent color, similar to embeds,
 * and mark the container as a spoiler.
 *
 * This can contain up to [MAX_COMPONENTS][net.dv8tion.jda.api.components.container.Container.MAX_COMPONENTS] [ContainerChildComponent].
 *
 * This requires [Components V2][net.dv8tion.jda.api.utils.messages.MessageRequest.useComponentsV2] to be enabled.
 *
 * @param uniqueId    Unique identifier of this component
 * @param accentColor Color of the container's left side, you can use [rgb], [hsb] or [hex] for it
 * @param spoiler     Hides the file until the user clicks on it
 * @param block       Lambda allowing further configuration
 *
 * @see ContainerChildComponent
 */
inline fun Container(uniqueId: Int? = null, accentColor: Int? = null, spoiler: Boolean = false, block: InlineContainer.() -> Unit): Container =
    InlineContainer(uniqueId, accentColor, spoiler).apply(block).build()