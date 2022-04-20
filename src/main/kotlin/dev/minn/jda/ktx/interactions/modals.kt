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


package dev.minn.jda.ktx.interactions

import dev.minn.jda.ktx.ComponentAccumulator
import dev.minn.jda.ktx.messages.Components
import net.dv8tion.jda.api.interactions.callbacks.IModalCallback
import net.dv8tion.jda.api.interactions.components.ActionRow
import net.dv8tion.jda.api.interactions.components.LayoutComponent
import net.dv8tion.jda.api.interactions.components.Modal
import net.dv8tion.jda.api.interactions.components.text.TextInput
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle
import net.dv8tion.jda.api.requests.restaction.interactions.ModalCallbackAction


fun IModalCallback.replyModal(
    id: String,
    title: String,
    components: Components = emptyList(),
    builder: InlineModal.() -> Unit = {}
): ModalCallbackAction {
    return replyModal(Modal(id, title, components, builder))
}


fun ModalBuilder(
    id: String,
    title: String,
    components: Components = emptyList(),
    builder: InlineModal.() -> Unit = {}
) = InlineModal(Modal.create(id, title)).also {
    it.configuredComponents.addAll(components)
    it.builder()
}

fun Modal(
    id: String,
    title: String,
    components: Components = emptyList(),
    builder: InlineModal.() -> Unit = {}
) = ModalBuilder(id, title, components, builder).build()


class InlineModal(val builder: Modal.Builder) {

    internal val configuredComponents = mutableListOf<LayoutComponent>()
    val components: ComponentAccumulator = ComponentAccumulator(configuredComponents)

    var id: String
        get() = builder.id
        set(value) {
            builder.id = value
        }

    var title: String
        get() = builder.title
        set(value) {
            builder.title = value
        }

    fun paragraph(
        id: String,
        label: String,
        required: Boolean = TextInputDefaults.required,
        value: String? = TextInputDefaults.value,
        placeholder: String? = TextInputDefaults.placeholder,
        requiredLength: IntRange? = TextInputDefaults.requiredLength,
        builder: TextInput.Builder.() -> Unit = {}
    ) {
        val text = TextInput.create(id, label, TextInputStyle.PARAGRAPH)
        text.isRequired = required
        text.value = value
        text.placeholder = placeholder
        requiredLength?.let {
            text.setRequiredRange(it.first, it.last)
        }
        configuredComponents.add(row(text.apply(builder).build()))
    }

    fun short(
        id: String,
        label: String,
        required: Boolean = TextInputDefaults.required,
        value: String? = TextInputDefaults.value,
        placeholder: String? = TextInputDefaults.placeholder,
        requiredLength: IntRange? = TextInputDefaults.requiredLength,
        builder: TextInput.Builder.() -> Unit = {}
    ) {
        val text = TextInput.create(id, label, TextInputStyle.SHORT)
        text.isRequired = required
        text.value = value
        text.placeholder = placeholder
        requiredLength?.let {
            text.setRequiredRange(it.first, it.last)
        }
        configuredComponents.add(row(text.apply(builder).build()))
    }

    fun build(): Modal = builder.addActionRows(configuredComponents.mapNotNull { it as? ActionRow }).build()
}
