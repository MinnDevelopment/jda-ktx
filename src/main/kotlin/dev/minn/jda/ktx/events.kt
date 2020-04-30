package dev.minn.jda.ktx

import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.GenericEvent

/**
 * Requires [CoroutineEventManager] to be used!
 */
inline fun <reified T : GenericEvent> JDA.listener(crossinline consumer: suspend (T) -> Unit): CoroutineEventListener {
    return object : CoroutineEventListener {
        override suspend fun onEvent(event: GenericEvent) {
            if (event is T)
                consumer(event)
        }
    }.also { addEventListener(it) }
}