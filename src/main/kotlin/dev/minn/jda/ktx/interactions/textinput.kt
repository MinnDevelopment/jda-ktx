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

/**
 * Defaults used for text inputs.
 *
 * These can be changed but keep in mind they will apply globally.
 */
object TextInputDefaults {
    var required = true
    var value: String? = null
    var placeholder: String? = null
    var requiredLength: IntRange? = null
}

/**
 * Creates a new [InlineTextInput] builder for the provided parameters.
 *
 * This is a kotlin idiomatic replacement for [TextInput.Builder]
 *
 * Uses [TextInputDefaults] for default values.
 *
 * @param[id] The component id used for events
 * @param[label] The label to display above the input box
 * @param[style] The [TextInputStyle]
 * @param[required] Whether the user must provide an input for this field (uses requiredLength)
 * @param[value] The prepopulated value for the input (this will be shown as typed input that the user can replace or keep)
 * @param[placeholder] The placeholder text to display when the user hasn't typed anything yet
 * @param[requiredLength] The minimum and maximum length of the input (Example: 2..1024)
 * @param[builder] Inline builder function
 *
 * @return [InlineTextInput]
 *
 * @see TextInput
 */
inline fun TextInputBuilder(
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

/**
 * Creates a new [TextInput] for the provided parameters.
 *
 * Uses [TextInputDefaults] for default values.
 *
 * @param[id] The component id used for events
 * @param[label] The label to display above the input box
 * @param[style] The [TextInputStyle]
 * @param[required] Whether the user must provide an input for this field (uses requiredRange)
 * @param[value] The prepopulated value for the input (this will be shown as typed input that the user can replace or keep)
 * @param[placeholder] The placeholder text to display when the user hasn't typed anything yet
 * @param[requiredLength] The minimum and maximum length of the input (Example: 2..1024)
 * @param[builder] Inline builder function
 *
 * @return [TextInput]
 *
 * @see TextInputBuilder
 */
inline fun TextInput(
        id: String,
        label: String,
        style: TextInputStyle,
        required: Boolean = TextInputDefaults.required,
        value: String? = TextInputDefaults.value,
        placeholder: String? = TextInputDefaults.placeholder,
        requiredLength: IntRange? = TextInputDefaults.requiredLength,
        builder: InlineTextInput.() -> Unit = {}
) = TextInputBuilder(id, label, style, required, value, placeholder, requiredLength, builder).build()

/**
 * Kotlin idiomatic builder for [TextInput]
 */
class InlineTextInput(val builder: TextInput.Builder) {
    /** Delegated property for [TextInput.Builder.setId] */
    var id: String
        get() = builder.id
        set(value) {
            builder.id = value
        }

    /** Delegated property for [TextInput.Builder.setLabel] */
    var label: String
        get() = builder.label
        set(value) {
            builder.label = value
        }

    /** Delegated property for [TextInput.Builder.setStyle] */
    var style: TextInputStyle
        get() = builder.style
        set(value) {
            builder.style = value
        }

    /** Delegated property for [TextInput.Builder.setRequired] */
    var required: Boolean
        get() = builder.isRequired
        set(value) {
            builder.isRequired = value
        }

    /** Delegated property for [TextInput.Builder.setValue] */
    var value: String?
        get() = builder.value
        set(value) {
            builder.value = value
        }

    /** Delegated property for [TextInput.Builder.setPlaceholder] */
    var placeholder: String?
        get() = builder.placeholder
        set(value) {
            builder.placeholder = value
        }

    /** Delegated property for [TextInput.Builder.setRequiredRange] */
    var requiredLength: IntRange
        get() = builder.minLength..builder.maxLength
        set(value) {
            builder.setRequiredRange(value.first, value.last)
        }

    /**
     * Builds the [TextInput]
     *
     * @return [TextInput]
     */
    fun build() = builder.build()
}