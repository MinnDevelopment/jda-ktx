/*
 * Copyright 2015 Austin Keener, Michael Ritter, Florian Spieß, and the JDA contributors
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

import net.dv8tion.jda.api.interactions.components.ActionRow
import net.dv8tion.jda.api.interactions.components.ItemComponent
import net.dv8tion.jda.api.interactions.components.LayoutComponent

/**
 * Returns new collection instance of this layout with all the components in set to disabled/enabled
 */
fun <T : LayoutComponent> Iterable<T>.withDisabled(disabled: Boolean) = map {
    it.withDisabled(disabled)
}

/**
 * Returns new collection instance of this layout with all the components in set to disabled
 */
fun <T : LayoutComponent> Iterable<T>.asDisabled() = withDisabled(true)

/**
 * Returns new collection instance of this layout with all the components in set to enabled
 */
fun <T : LayoutComponent> Iterable<T>.asEnabled() = withDisabled(false)

/**
 * Returns new collection instance of this layout with all the components in set to disabled/enabled
 */
fun <T : LayoutComponent> Sequence<T>.withDisabled(disabled: Boolean) = map {
    it.withDisabled(disabled)
}

/**
 * Returns new collection instance of this layout with all the components in set to disabled
 */
fun <T : LayoutComponent> Sequence<T>.asDisabled() = withDisabled(true)

/**
 * Returns new collection instance of this layout with all the components in set to enabled
 */
fun <T : LayoutComponent> Sequence<T>.asEnabled() = withDisabled(false)

/**
 * Construct an [ActionRow] from the provided components
 */
fun row(vararg components: ItemComponent) = ActionRow.of(*components)

/**
 * Construct an [ActionRow] from the provided components
 */
fun Collection<ItemComponent>.row() = ActionRow.of(this)