/*
 * Copyright 2020 Florian Spieß
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package dev.minn.jda.ktx.interactions.components

import net.dv8tion.jda.api.components.actionrow.ActionRow
import net.dv8tion.jda.api.components.textinput.TextInput
import net.dv8tion.jda.api.components.textinput.TextInputStyle
import net.dv8tion.jda.api.interactions.callbacks.IModalCallback
import net.dv8tion.jda.api.interactions.modals.Modal
import net.dv8tion.jda.api.interactions.modals.ModalTopLevelComponent
import net.dv8tion.jda.api.requests.restaction.interactions.ModalCallbackAction

/**
 * Send a modal with a kotlin idiomatic builder.
 *
 * @param[id] The modal id used for event handling
 * @param[title] The title of the modal
 * @param[components] The components for the modal
 * @param[builder] Idiomatic builder function for the modal
 *
 * @return The [ModalCallbackAction] which acknowledges the interaction
 */
fun IModalCallback.replyModal(
    id: String,
    title: String,
    components: Collection<ModalTopLevelComponent> = emptyList(),
    builder: InlineModal.() -> Unit = {}
): ModalCallbackAction {
    return replyModal(Modal(id, title, components, builder))
}

/**
 * Creates a new [InlineModal] builder for the provided parameters.
 *
 * @param[id] The modal id used for event handling
 * @param[title] The title of the modal
 * @param[components] The components for the modal
 * @param[builder] Idiomatic builder function for the modal
 *
 * @return The [InlineModal] builder
 *
 * @see    [Modal]
 */
fun ModalBuilder(
    id: String,
    title: String,
    components: Collection<ModalTopLevelComponent> = emptyList(),
    builder: InlineModal.() -> Unit = {}
) = InlineModal(Modal.create(id, title)).also {
    it.configuredComponents.addAll(components)
    it.builder()
}

/**
 * Creates a new [Modal] for the provided parameters.
 *
 * @param[id] The modal id used for event handling
 * @param[title] The title of the modal
 * @param[components] The components for the modal
 * @param[builder] Idiomatic builder function for the modal
 *
 * @return The [Modal] instance
 */
fun Modal(
    id: String,
    title: String,
    components: Collection<ModalTopLevelComponent> = emptyList(),
    builder: InlineModal.() -> Unit = {}
) = ModalBuilder(id, title, components, builder).build()

/**
 * Kotlin idiomatic builder for [Modals][Modal].
 */
class InlineModal(val builder: Modal.Builder) {
    internal val configuredComponents = mutableListOf<ModalTopLevelComponent>()

    /**
     * Components added to the modal.
     *
     * Allows `+=` syntax for adding components.
     */
    val components: ModalComponentAccumulator = ModalComponentAccumulator(configuredComponents)

    /** Delegated property for [Modal.Builder.setId] */
    var id: String
        get() = builder.id
        set(value) {
            builder.id = value
        }

    /** Delegated property for [Modal.Builder.setTitle] */
    var title: String
        get() = builder.title
        set(value) {
            builder.title = value
        }

    /** Adds a [TextInput] with [TextInputStyle.PARAGRAPH] to the modal */
    fun paragraph(
        id: String,
        label: String,
        uniqueId: Int? = null,
        required: Boolean = TextInputDefaults.required,
        value: String? = TextInputDefaults.value,
        placeholder: String? = TextInputDefaults.placeholder,
        requiredLength: IntRange? = TextInputDefaults.requiredLength,
        builder: TextInput.Builder.() -> Unit = {}
    ) {
        val text = TextInput.create(id, label, TextInputStyle.PARAGRAPH)
        if (uniqueId != null)
            text.uniqueId = uniqueId
        text.isRequired = required
        text.value = value
        text.placeholder = placeholder
        requiredLength?.let {
            text.setRequiredRange(it.first, it.last)
        }
        configuredComponents.add(row(text.apply(builder).build()))
    }

    /** Adds a [TextInput] with [TextInputStyle.SHORT] to the modal */
    fun short(
        id: String,
        label: String,
        uniqueId: Int? = null,
        required: Boolean = TextInputDefaults.required,
        value: String? = TextInputDefaults.value,
        placeholder: String? = TextInputDefaults.placeholder,
        requiredLength: IntRange? = TextInputDefaults.requiredLength,
        builder: TextInput.Builder.() -> Unit = {}
    ) {
        val text = TextInput.create(id, label, TextInputStyle.SHORT)
        if (uniqueId != null)
            text.uniqueId = uniqueId
        text.isRequired = required
        text.value = value
        text.placeholder = placeholder
        requiredLength?.let {
            text.setRequiredRange(it.first, it.last)
        }
        configuredComponents.add(row(text.apply(builder).build()))
    }

    /**
     * Builds the [Modal]
     *
     * @return The [Modal] instance
     */
    fun build(): Modal = builder.addComponents(configuredComponents.mapNotNull { it as? ActionRow }).build()
}

class ModalComponentAccumulator(private val config: MutableList<ModalTopLevelComponent>) {
    operator fun plusAssign(components: Collection<ModalTopLevelComponent>) {
        config += components
    }

    operator fun plusAssign(component: ModalTopLevelComponent) {
        config += component
    }

    operator fun minusAssign(components: Collection<ModalTopLevelComponent>) {
        config -= components.toSet()
    }

    operator fun minusAssign(component: ModalTopLevelComponent) {
        config -= component
    }
}
