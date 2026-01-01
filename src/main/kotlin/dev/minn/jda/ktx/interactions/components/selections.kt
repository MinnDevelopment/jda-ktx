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

import net.dv8tion.jda.api.components.selections.EntitySelectMenu
import net.dv8tion.jda.api.components.selections.EntitySelectMenu.SelectTarget
import net.dv8tion.jda.api.components.selections.SelectOption
import net.dv8tion.jda.api.components.selections.StringSelectMenu
import net.dv8tion.jda.api.entities.emoji.Emoji

fun SelectOption(label: String, value: String, description: String? = null, emoji: Emoji? = null, default: Boolean = false)
    = SelectOption.of(label, value)
        .withDescription(description)
        .withEmoji(emoji)
        .withDefault(default)

fun StringSelectMenu.Builder.option(label: String, value: String, description: String? = null, emoji: Emoji? = null, default: Boolean = false)
    = addOptions(SelectOption(label, value, description, emoji, default))

inline fun StringSelectMenu(
    customId: String,
    uniqueId: Int = -1,
    placeholder: String? = null,
    valueRange: IntRange = 1..1,
    disabled: Boolean = false,
    options: Collection<SelectOption> = emptyList(),
    builder: StringSelectMenu.Builder.() -> Unit = {},
) = StringSelectMenu.create(customId).let {
    if (uniqueId != -1)
        it.uniqueId = uniqueId
    if (placeholder != null)
        it.placeholder = placeholder
    it.setRequiredRange(valueRange.first, valueRange.last)
    if (disabled)
        it.isDisabled = true
    it.options.addAll(options)
    it.apply(builder)
    it.build()
}

inline fun EntitySelectMenu(
    customId: String,
    types: Collection<SelectTarget>,
    uniqueId: Int = -1,
    placeholder: String? = null,
    valueRange: IntRange = 1..1,
    disabled: Boolean = false,
    builder: EntitySelectMenu.Builder.() -> Unit = {},
) = EntitySelectMenu.create(customId, types).let {
    if (uniqueId != -1)
        it.uniqueId = uniqueId
    if (placeholder != null)
        it.placeholder = placeholder
    it.setRequiredRange(valueRange.first, valueRange.last)
    if (disabled)
        it.isDisabled = true
    it.apply(builder)
    it.build()
}

fun SelectTarget.into() = listOf(this)

fun SelectTarget.menu(
    customId: String,
    uniqueId: Int = -1,
    placeholder: String? = null,
    valueRange: IntRange = 1..1,
    disabled: Boolean = false,
    builder: EntitySelectMenu.Builder.() -> Unit = {}
) = EntitySelectMenu(customId, into(), uniqueId, placeholder, valueRange, disabled, builder)

fun Collection<SelectTarget>.menu(
    customId: String,
    uniqueId: Int = -1,
    placeholder: String? = null,
    valueRange: IntRange = 1..1,
    disabled: Boolean = false,
    builder: EntitySelectMenu.Builder.() -> Unit = {}
) = EntitySelectMenu(customId, this, uniqueId, placeholder, valueRange, disabled, builder)
