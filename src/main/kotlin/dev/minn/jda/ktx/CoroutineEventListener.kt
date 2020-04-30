package dev.minn.jda.ktx

import net.dv8tion.jda.api.events.GenericEvent

interface CoroutineEventListener {
    suspend fun onEvent(event: GenericEvent)
}