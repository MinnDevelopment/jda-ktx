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

package dev.minn.jda.ktx

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.hooks.EventListener
import net.dv8tion.jda.api.hooks.IEventManager
import java.util.concurrent.CopyOnWriteArrayList

/**
 * EventManager implementation which supports both [EventListener] and [CoroutineEventListener].
 *
 * This enables [the coroutine listener extension][listener].
 */
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
