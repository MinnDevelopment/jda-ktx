package dev.minn.jda.ktx.interactions.components.utils

internal fun <T : Any> T?.checkInit(name: String): T {
    if (this == null) throw UninitializedPropertyAccessException("$name is not initialized")
    return this
}

@Suppress("UnusedReceiverParameter")
internal fun Any.checkInit() {
    // Nothing, supposed to be used on non-backing properties with lateinit/nullable backers
}
