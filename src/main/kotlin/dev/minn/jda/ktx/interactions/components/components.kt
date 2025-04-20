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

import net.dv8tion.jda.api.components.Component
import net.dv8tion.jda.api.components.actionrow.ActionRow
import net.dv8tion.jda.api.components.actionrow.ActionRowChildComponent
import net.dv8tion.jda.api.components.tree.ComponentTree

/**
 * Returns new collection instance of this layout with all the components in set to disabled/enabled
 */
@Suppress("UNCHECKED_CAST")
fun <T : Component> Iterable<T>.withDisabled(disabled: Boolean): List<T> =
    ComponentTree.of(this.toList()).withDisabled(disabled).components as List<T>

/**
 * Returns new collection instance of this layout with all the components in set to disabled
 */
fun <T : Component> Iterable<T>.asDisabled() = withDisabled(true)

/**
 * Returns new collection instance of this layout with all the components in set to enabled
 */
fun <T : Component> Iterable<T>.asEnabled() = withDisabled(false)

/**
 * Returns new collection instance of this layout with all the components in set to disabled/enabled
 */
fun <T : Component> Sequence<T>.withDisabled(disabled: Boolean) = asIterable().withDisabled(disabled).asSequence()

/**
 * Returns new collection instance of this layout with all the components in set to disabled
 */
fun <T : Component> Sequence<T>.asDisabled() = withDisabled(true)

/**
 * Returns new collection instance of this layout with all the components in set to enabled
 */
fun <T : Component> Sequence<T>.asEnabled() = withDisabled(false)

/**
 * Construct an [ActionRow] from the provided components
 */
fun row(vararg components: ActionRowChildComponent) = ActionRow.of(*components)

/**
 * Construct an [ActionRow] from the provided components
 */
fun Collection<ActionRowChildComponent>.row() = ActionRow.of(this)

abstract class InlineComponentWithChildren<T> {
    var components = arrayListOf<T>()

    operator fun T.unaryPlus() {
        components += this
    }

    operator fun Collection<T>.unaryPlus() {
        components += this
    }
}