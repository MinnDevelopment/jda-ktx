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

import net.dv8tion.jda.api.entities.Emoji
import net.dv8tion.jda.api.interactions.components.selections.SelectOption
import net.dv8tion.jda.api.interactions.components.selections.SelectionMenu

fun SelectOption(label: String, value: String, description: String? = null, emoji: Emoji? = null, default: Boolean = false)
    = SelectOption.of(label, value)
        .withDescription(description)
        .withEmoji(emoji)
        .withDefault(default)

fun SelectionMenu.Builder.option(label: String, value: String, description: String? = null, emoji: Emoji? = null, default: Boolean = false)
    = addOptions(SelectOption(label, value, description, emoji, default))

inline fun SelectionMenu(
    customId: String,
    placeholder: String? = null,
    valueRange: IntRange = 1..1,
    disabled: Boolean = false,
    options: Collection<SelectOption> = emptyList(),
    builder: SelectionMenu.Builder.() -> Unit = {}
): SelectionMenu {
    return SelectionMenu.create(customId).let {
        it.placeholder = placeholder
        it.setRequiredRange(valueRange.first, valueRange.last)
        it.isDisabled = disabled
        it.options.addAll(options)
        it.apply(builder)
        it.build()
    }
}