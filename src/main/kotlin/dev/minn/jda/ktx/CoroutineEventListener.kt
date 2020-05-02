package dev.minn.jda.ktx

import net.dv8tion.jda.api.events.GenericEvent

/**
 * Identical to [EventListener][net.dv8tion.jda.api.hooks.EventListener] but uses suspending function.
 */
interface CoroutineEventListener {
    suspend fun onEvent(event: GenericEvent)
}