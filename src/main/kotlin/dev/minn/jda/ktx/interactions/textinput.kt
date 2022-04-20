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

import net.dv8tion.jda.api.interactions.components.text.TextInput
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle


object TextInputDefaults {
    var required = true
    var value: String? = null
    var placeholder: String? = null
    var requiredLength: IntRange? = null
}

fun TextInputBuilder(
        id: String,
        label: String,
        style: TextInputStyle,
        required: Boolean = TextInputDefaults.required,
        value: String? = TextInputDefaults.value,
        placeholder: String? = TextInputDefaults.placeholder,
        requiredLength: IntRange? = TextInputDefaults.requiredLength,
        builder: InlineTextInput.() -> Unit = {}
): InlineTextInput = InlineTextInput(TextInput.create(id, label, style)).also {
    it.required = required
    it.value = value
    it.placeholder = placeholder
    requiredLength?.apply {
        it.requiredLength = this
    }
    it.builder()
}

fun TextInput(
        id: String,
        label: String,
        style: TextInputStyle,
        required: Boolean = TextInputDefaults.required,
        value: String? = TextInputDefaults.value,
        placeholder: String? = TextInputDefaults.placeholder,
        requiredLength: IntRange? = TextInputDefaults.requiredLength,
        builder: InlineTextInput.() -> Unit = {}
) = TextInputBuilder(id, label, style, required, value, placeholder, requiredLength, builder).build()


class InlineTextInput(val builder: TextInput.Builder) {
    var id: String
        get() = builder.id
        set(value) {
            builder.id = value
        }

    var label: String
        get() = builder.label
        set(value) {
            builder.label = value
        }

    var style: TextInputStyle
        get() = builder.style
        set(value) {
            builder.style = value
        }

    var required: Boolean
        get() = builder.isRequired
        set(value) {
            builder.isRequired = value
        }

    var value: String?
        get() = builder.value
        set(value) {
            builder.value = value
        }

    var placeholder: String?
        get() = builder.placeholder
        set(value) {
            builder.placeholder = value
        }

    var requiredLength: IntRange
        get() = builder.minLength..builder.maxLength
        set(value) {
            builder.setRequiredRange(value.first, value.last)
        }

    fun build() = builder.build()
}