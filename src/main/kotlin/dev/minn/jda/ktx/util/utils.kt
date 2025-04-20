package dev.minn.jda.ktx.util

internal inline fun <T, U> T.applyIfNotNull(value: U?, block: T.(U) -> T): T {
    return when {
        value != null -> block(value)
        else -> this
    }
}