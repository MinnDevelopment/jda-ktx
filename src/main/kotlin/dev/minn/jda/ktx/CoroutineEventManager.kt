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

@file:Suppress("MemberVisibilityCanBePrivate")

package dev.minn.jda.ktx

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.hooks.IEventManager
import net.dv8tion.jda.api.hooks.InterfacedEventManager
import java.util.concurrent.CopyOnWriteArrayList

/**
 * EventManager which works via a delegate to support [CoroutineEventListener].
 *
 * This enables [the coroutine listener extension][listener].
 *
 * @property delegate
 * @property scope
 * @constructor Create empty Coroutine event manager
 */
class CoroutineEventManager(
    val delegate: IEventManager = InterfacedEventManager(),
    private val scope: CoroutineScope = GlobalScope
) : IEventManager {
    private val listeners = CopyOnWriteArrayList<CoroutineEventListener>()
    
    override fun handle(event: GenericEvent) {
        scope.launch {
            for (listener in listeners) {
                when (listener) {
                    is CoroutineEventListener -> listener.onEvent(event)
                    else                      -> delegate.handle(event)
                }
            }
        }
    }
    
    override fun register(listener: Any) {
        when (listener) {
            is CoroutineEventListener -> listeners.add(listener)
            else                      -> delegate.register(listener)
        }
    }
    override fun getRegisteredListeners(): List<Any?> = listeners + delegate.registeredListeners
    override fun unregister(listener: Any) {
        when (listener) {
            is CoroutineEventListener -> listeners.remove(listener)
            else                      -> delegate.unregister(listener)
        }
    }
}
