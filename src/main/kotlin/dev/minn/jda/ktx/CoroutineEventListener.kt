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

import net.dv8tion.jda.api.events.GenericEvent

/**
 * Identical to [EventListener][net.dv8tion.jda.api.hooks.EventListener] but uses suspending function.
 */
interface CoroutineEventListener {
    /**
     * The timeout (in milliseconds) to use, or 0 to use event manager default.
     *
     * This timeout decides how long a listener function is allowed to run, not when to unregister it.
     */
    val timeout: Long get() = 0L

    suspend fun onEvent(event: GenericEvent)

    /**
     * Unregisters this listener
     */
    fun cancel() {}
}