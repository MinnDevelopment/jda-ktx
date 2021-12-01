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
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder
import net.dv8tion.jda.api.sharding.ShardManager

/**
 * Applies the [CoroutineEventManager] to this builder.
 */
fun JDABuilder.injectKTX(timeout: Long = -1) = setEventManager(CoroutineEventManager(timeout=timeout))

/**
 * Applies the [CoroutineEventManager] to this builder.
 */
fun DefaultShardManagerBuilder.injectKTX(timeout: Long = -1) = setEventManagerProvider { CoroutineEventManager(timeout=timeout) }

/**
 * The coroutine scope used by the underlying [CoroutineEventManager].
 * If this instance does not use the coroutine event manager, this returns [GlobalScope] instead.
 */
val JDA.scope: CoroutineScope get() = (eventManager as? CoroutineEventManager)?.scope ?: GlobalScope

/**
 * The coroutine scope used by the underlying [CoroutineEventManager].
 * If this instance does not use the coroutine event manager, this returns [GlobalScope] instead.
 *
 * @throws[NoSuchElementException] If no shards are currently available
 */
val ShardManager.scope: CoroutineScope get() = (shardCache.first().eventManager as? CoroutineEventManager)?.scope ?: GlobalScope