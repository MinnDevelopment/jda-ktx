/*
 * Copyright (c) 2021 Florian Spieß
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
 *
 */

package dev.minn.jda.ktx

import java.util.function.Predicate

internal class DelegatingCollection<T>(
    private val delegate: MutableCollection<T> = mutableListOf(),
    private val adder: (toAdd: T) -> Unit,
    private val remover: (toRemove: T) -> Unit,
) : MutableCollection<T> by delegate {
    override fun add(element: T): Boolean {
        adder(element)
        return delegate.add(element)
    }
    
    override fun addAll(elements: Collection<T>): Boolean {
        elements.forEach(adder)
        return delegate.addAll(elements)
    }
    
    override fun clear() {
        delegate.forEach(remover)
        delegate.clear()
    }
    
    override fun remove(element: T): Boolean {
        remover(element)
        return delegate.remove(element)
    }
    
    override fun removeAll(elements: Collection<T>): Boolean {
        elements.forEach(remover)
        return delegate.removeAll(elements)
    }
    
    override fun removeIf(filter: Predicate<in T>): Boolean {
        @Suppress("LABEL_NAME_CLASH")
        return delegate.removeIf { element ->
            if (filter.test(element)) {
                remover(element)
                return@removeIf true
            }
            return@removeIf false
        }
    }
    
    override fun retainAll(elements: Collection<T>): Boolean {
        return delegate.removeIf { element ->
            if (!elements.contains(element)) {
                remover(element)
                return@removeIf true
            }
            return@removeIf false
        }
    }
}
