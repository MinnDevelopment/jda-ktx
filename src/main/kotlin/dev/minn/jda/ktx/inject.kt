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

import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.hooks.IEventManager
import net.dv8tion.jda.api.hooks.InterfacedEventManager
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder
import java.util.function.IntFunction


/**
 * Applies the [CoroutineEventManager] to this builder.
 */
fun JDABuilder.injectKTX(delegate: IEventManager? = null) =
    setEventManager(CoroutineEventManager(delegate ?: InterfacedEventManager()))

/**
 * Applies the [CoroutineEventManager] to this builder.
 */
fun DefaultShardManagerBuilder.injectKTX(delegate: IEventManager? = null) =
    injectKTX { CoroutineEventManager(delegate ?: InterfacedEventManager()) }

/**
 * Applies the [CoroutineEventManager] to this builder.
 */
fun DefaultShardManagerBuilder.injectKTX(delegateProvider: IntFunction<out IEventManager>? = null) =
    setEventManagerProvider { id ->
        CoroutineEventManager(delegateProvider?.apply(id) ?: InterfacedEventManager())
    }