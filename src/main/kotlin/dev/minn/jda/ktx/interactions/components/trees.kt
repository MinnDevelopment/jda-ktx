package dev.minn.jda.ktx.interactions.components

import net.dv8tion.jda.api.components.Component
import net.dv8tion.jda.api.components.IComponentUnion
import net.dv8tion.jda.api.components.tree.ComponentTree
import kotlin.jvm.optionals.getOrNull

/**
 * Creates a [ComponentTree] from this collection, and checks they are compatible with [T].
 *
 * @throws IllegalArgumentException If a component cannot be represented by [T]
 */
inline fun <reified T : IComponentUnion, E : Component> Collection<E>.toComponentTree(): ComponentTree<T> =
    ComponentTree.of(T::class.java, this)

/**
 * Creates a [ComponentTree] of [IComponentUnion] from this collection.
 */
fun Collection<Component>.toDefaultComponentTree(): ComponentTree<IComponentUnion> =
    ComponentTree.of(this)

/**
 * Finds all components of type [T], recursively.
 */
inline fun <reified T : Component> ComponentTree<*>.findAll(): List<T> =
    findAll(T::class.java)

/**
 * Finds all components of type [T] satisfying the [filter], recursively.
 */
inline fun <reified T : Component> ComponentTree<*>.findAll(crossinline filter: (T) -> Boolean): List<T> =
    findAll(T::class.java) { filter(it) }

/**
 * Finds the first component of type [T] satisfying the [filter], recursively.
 */
inline fun <reified T : Component> ComponentTree<*>.find(crossinline filter: (T) -> Boolean): T? =
    find(T::class.java) { filter(it) }.getOrNull()
