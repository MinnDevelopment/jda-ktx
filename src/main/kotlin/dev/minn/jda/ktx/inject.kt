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


/**
 * Applies the [CoroutineEventManager] to this builder.
 *
 * @param delegate A delegate passed to [CoroutineEventManager], allowing any [Event Manager][IEventManager] type to be used.
 * Defaults to `InterfacedEventManager()`
 */
fun JDABuilder.injectKTX(delegate: IEventManager = InterfacedEventManager()) =
    setEventManager(CoroutineEventManager(delegate))

/**
 * Applies the [CoroutineEventManager] to this builder.
 *
 * @param delegateProvider Provider for a delegate passed to [CoroutineEventManager], allowing any [Event Manager][IEventManager] type to be used.
 * Defaults to `InterfacedEventManager()`
 * @receiver id of the shard to provide an [Event Manager][IEventManager] for.
 */
fun DefaultShardManagerBuilder.injectKTX(delegateProvider: (Int) -> IEventManager = { InterfacedEventManager() }) =
    setEventManagerProvider { id ->
        CoroutineEventManager(delegateProvider(id))
    }