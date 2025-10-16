package dev.minn.jda.ktx.interactions.components

import dev.minn.jda.ktx.interactions.components.utils.checkInit
import net.dv8tion.jda.api.components.Component
import net.dv8tion.jda.api.components.label.Label
import net.dv8tion.jda.api.components.label.LabelChildComponent
import net.dv8tion.jda.api.components.textinput.TextInput
import net.dv8tion.jda.api.components.textinput.TextInputStyle

private val DUMMY_LABEL = Label.of("label", TextInput.of("id", TextInputStyle.SHORT))

class InlineLabel : InlineComponent {

    private var component: Label = DUMMY_LABEL

    override var uniqueId: Int
        get() = component.uniqueId
        set(value) {
            component = component.withUniqueId(value)
        }

    /** The label of this Label, see [Label.withLabel] */
    private var _label: String? = null
    var label: String
        get() = _label.checkInit("label content")
        set(value) {
            component = component.withLabel(value)
            _label = value
        }

    val hasLabel: Boolean get() = _label != null

    /** The description of this Label, see [Label.withDescription] */
    var description: String?
        get() = component.description
        set(value) {
            component = component.withDescription(value)
        }

    private var _child: LabelChildComponent? = null
    /** The child contained by this Label, see [Label.withChild] */
    var child: LabelChildComponent
        get() = _child.checkInit("child")
        set(value) {
            component = component.withChild(value)
            _child = value
        }

    val hasChild: Boolean get() = _child != null

    fun build(): Label {
        label.checkInit()
        child.checkInit()
        return component
    }
}

/**
 * Component that contains a label, an optional description,
 * and a [child component][LabelChildComponent], see [Label][net.dv8tion.jda.api.components.label.Label].
 *
 * @param label       Label of the Label, see [Label.withLabel]
 * @param uniqueId    Unique identifier of this component, see [Component.withUniqueId]
 * @param description The description of this Label, see [Label.withDescription]
 * @param child       The child contained by this Label, see [Label.withChild]
 * @param block       Lambda allowing further configuration
 */
inline fun Label(
    label: String? = null,
    uniqueId: Int = -1,
    description: String? = null,
    child: LabelChildComponent? = null,
    block: InlineLabel.() -> Unit = {},
): Label {
    return InlineLabel()
        .apply {
            if (label != null) this.label = label
            if (uniqueId != -1) this.uniqueId = uniqueId
            if (description != null) this.description = description
            if (child != null) this.child = child
            block()
        }
        .build()
}
