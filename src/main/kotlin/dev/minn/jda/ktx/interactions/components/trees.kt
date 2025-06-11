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
 * Creates a [ComponentTree] of [ComponentUnion] from this collection.
 */
fun Collection<Component>.toDefaultComponentTree(): ComponentTree<IComponentUnion> =
    ComponentTree.of(this)

/**
 * Recursively finds all components of the [T] type.
 */
inline fun <reified T : Component> ComponentTree<*>.findAll(): List<T> =
    findAll(T::class.java)

/**
 * Recursively finds all components of the [T] type and satisfying the provided [filter].
 */
inline fun <reified T : Component> ComponentTree<*>.findAll(crossinline filter: (T) -> Boolean): List<T> =
    findAll(T::class.java) { filter(it) }

// TODO: docs
inline fun <reified T : Component> ComponentTree<*>.find(crossinline filter: (T) -> Boolean): T? =
    find(T::class.java) { filter(it) }.getOrNull()