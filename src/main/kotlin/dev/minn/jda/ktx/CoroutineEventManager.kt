package dev.minn.jda.ktx

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.hooks.EventListener
import net.dv8tion.jda.api.hooks.IEventManager
import java.util.concurrent.CopyOnWriteArrayList

class CoroutineEventManager(
    private val scope: CoroutineScope = GlobalScope
) : IEventManager {
    private val listeners = CopyOnWriteArrayList<Any>()

    override fun handle(event: GenericEvent) {
        scope.launch {
            for (listener in listeners) {
                when (listener) {
                    is CoroutineEventListener -> listener.onEvent(event)
                    is EventListener -> listener.onEvent(event)
                }
            }
        }
    }

    override fun register(listener: Any) {
        listeners.add(when (listener) {
            is EventListener, is CoroutineEventListener -> listener
            else -> throw IllegalArgumentException("Listener must implement either EventListener or CoroutineEventListener")
        })
    }

    override fun getRegisteredListeners(): MutableList<Any> = mutableListOf(listeners)

    override fun unregister(listener: Any) {
        listeners.remove(listener)
    }
}
