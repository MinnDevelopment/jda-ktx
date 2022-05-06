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

package dev.minn.jda.ktx.jdabuilder

import dev.minn.jda.ktx.events.CoroutineEventManager
import dev.minn.jda.ktx.events.getDefaultScope
import kotlinx.coroutines.CoroutineScope
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder
import net.dv8tion.jda.api.sharding.ShardManager
import kotlin.time.Duration

/**
 * Applies the [CoroutineEventManager] to this builder.
 */
fun JDABuilder.injectKTX(timeout: Duration = Duration.INFINITE) = setEventManager(CoroutineEventManager(timeout=timeout))

/**
 * Applies the [CoroutineEventManager] to this builder.
 */
fun DefaultShardManagerBuilder.injectKTX(timeout: Duration = Duration.INFINITE) = setEventManagerProvider { CoroutineEventManager(timeout=timeout) }

/**
 * The coroutine scope used by the underlying [CoroutineEventManager].
 * If this instance does not use the coroutine event manager, this returns the default scope from [getDefaultScope].
 */
val JDA.scope: CoroutineScope get() = (eventManager as? CoroutineEventManager) ?: getDefaultScope()

/**
 * The coroutine scope used by the underlying [CoroutineEventManager].
 * If this instance does not use the coroutine event manager, this returns the default scope from [getDefaultScope].
 */
val ShardManager.scope: CoroutineScope get() = (shardCache.firstOrNull()?.eventManager as? CoroutineEventManager) ?: getDefaultScope()