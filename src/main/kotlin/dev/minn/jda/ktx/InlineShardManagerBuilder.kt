/*
 * Copyright (c) 2021 Florian Spieß
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
 *
 */

@file:Suppress("MemberVisibilityCanBePrivate", "unused", "FunctionName")

package dev.minn.jda.ktx

import com.neovisionaries.ws.client.WebSocketFactory
import net.dv8tion.jda.api.OnlineStatus
import net.dv8tion.jda.api.audio.factory.IAudioSendFactory
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.hooks.IEventManager
import net.dv8tion.jda.api.hooks.VoiceDispatchInterceptor
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder
import net.dv8tion.jda.api.sharding.ShardManager
import net.dv8tion.jda.api.sharding.ThreadPoolProvider
import net.dv8tion.jda.api.utils.ChunkingFilter
import net.dv8tion.jda.api.utils.Compression
import net.dv8tion.jda.api.utils.MemberCachePolicy
import net.dv8tion.jda.api.utils.SessionController
import net.dv8tion.jda.api.utils.cache.CacheFlag
import okhttp3.OkHttpClient
import java.util.concurrent.ConcurrentMap
import java.util.concurrent.ExecutorService
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ThreadFactory
import java.util.function.IntFunction


/**
 * Creates a new [ShardManager] instance with the default settings by using [DefaultShardManagerBuilder.createDefault]
 *
 * @param token The token to log in with. If set to null, assign a value to [InlineShardManagerBuilder.token].
 * @param login Whether or not to automatically log in. See: [ShardManager.login]
 * @param builder The function used to configure the builder.
 * @return The new [ShardManager] instance.
 * @see DefaultShardManagerBuilder.createDefault
 */
inline fun DefaultShardManager(
    token: String? = null,
    login: Boolean = true,
    builder: InlineShardManagerBuilder.() -> Unit
                              ) = InlineShardManagerBuilder(DefaultShardManagerBuilder.createDefault(token))
        .also(builder)
        .build(login)

/**
 * Creates a new [ShardManager] instance with the default settings by using [DefaultShardManagerBuilder.createDefault]
 *
 * @param token The token to log in with. If set to null, assign a value to [InlineShardManagerBuilder.token].
 * @param login Whether or not to automatically log in. See: [ShardManager.login]
 * @param intents A list of intents to enable.
 * @param builder The function used to configure the builder.
 * @return The new [ShardManager] instance.
 * @see DefaultShardManagerBuilder.createDefault
 */
inline fun DefaultShardManager(
    token: String? = null,
    login: Boolean = true,
    vararg intents: GatewayIntent,
    builder: InlineShardManagerBuilder.() -> Unit
                              ) =
    InlineShardManagerBuilder(DefaultShardManagerBuilder.createDefault(token, intents.toList()))
            .also(builder)
            .build(login)

/**
 * Creates a new [ShardManager] instance with the default settings by using [DefaultShardManagerBuilder.createDefault]
 *
 * @param token The token to log in with. If set to null, assign a value to [InlineShardManagerBuilder.token].
 * @param builder The function used to configure the builder.
 * @return The [ShardManager] instance. You must call [DefaultShardManagerBuilder.build] on this to finish constructing the ShardManager.
 * @see DefaultShardManagerBuilder.createDefault
 */
inline fun DefaultShardManagerBuilder(
    token: String? = null,
    builder: InlineShardManagerBuilder.() -> Unit
                                     ) = InlineShardManagerBuilder(DefaultShardManagerBuilder.createDefault(token))
        .also(builder)

/**
 * Creates a new [ShardManager] instance with the default settings by using [DefaultShardManagerBuilder.createDefault]
 *
 * @param token The token to log in with. If set to null, assign a value to [InlineShardManagerBuilder.token].
 * @param intents A list of intents to enable.
 * @param builder The function used to configure the builder.
 * @return The [ShardManager] instance. You must call [DefaultShardManagerBuilder.build] on this to finish constructing the ShardManager.
 * @see DefaultShardManagerBuilder.createDefault
 */
inline fun DefaultShardManagerBuilder(
    token: String? = null,
    vararg intents: GatewayIntent,
    builder: InlineShardManagerBuilder.() -> Unit
                                     ) = InlineShardManagerBuilder(DefaultShardManagerBuilder.createDefault(token, intents.toList()))
        .also(builder)

/**
 * Creates a new [ShardManager] instance with the default settings by using [DefaultShardManagerBuilder.createLight]
 *
 * @param token The token to log in with. If set to null, assign a value to [InlineShardManagerBuilder.token].
 * @param login Whether or not to automatically log in. See: [ShardManager.login]
 * @param builder The function used to configure the builder.
 * @return The new [ShardManager] instance.
 * @see DefaultShardManagerBuilder.createLight
 */
inline fun LightShardManager(
    token: String? = null,
    login: Boolean = true,
    builder: InlineShardManagerBuilder.() -> Unit
                            ) = InlineShardManagerBuilder(DefaultShardManagerBuilder.createLight(token))
        .also(builder)
        .build(login)

/**
 * Creates a new [ShardManager] instance with the default settings by using [DefaultShardManagerBuilder.createLight]
 *
 * @param token The token to log in with. If set to null, assign a value to [InlineShardManagerBuilder.token].
 * @param login Whether or not to automatically log in. See: [ShardManager.login]
 * @param intents A list of intents to enable.
 * @param builder The function used to configure the builder.
 * @return The new [ShardManager] instance.
 * @see DefaultShardManagerBuilder.createLight
 */
inline fun LightShardManager(
    token: String? = null,
    login: Boolean = true,
    vararg intents: GatewayIntent,
    builder: InlineShardManagerBuilder.() -> Unit
                            ) = InlineShardManagerBuilder(DefaultShardManagerBuilder.createLight(token, intents.toList()))
        .also(builder)
        .build(login)

/**
 * Creates a new [ShardManager] instance with the default settings by using [DefaultShardManagerBuilder.createLight]
 *
 * @param token The token to log in with. If set to null, assign a value to [InlineShardManagerBuilder.token].
 * @param builder The function used to configure the builder.
 * @return The [ShardManager] instance. You must call [DefaultShardManagerBuilder.build] on this to finish constructing the ShardManager.
 * @see DefaultShardManagerBuilder.createLight
 */
inline fun LightShardManagerBuilder(
    token: String? = null,
    builder: InlineShardManagerBuilder.() -> Unit
                                   ) = InlineShardManagerBuilder(DefaultShardManagerBuilder.createLight(token))
        .also(builder)

/**
 * Creates a new [ShardManager] instance with the default settings by using [DefaultShardManagerBuilder.createLight]
 *
 * @param token The token to log in with. If set to null, assign a value to [InlineShardManagerBuilder.token].
 * @param intents A list of intents to enable.
 * @param builder The function used to configure the builder.
 * @return The [ShardManager] instance. You must call [DefaultShardManagerBuilder.build] on this to finish constructing the ShardManager.
 * @see DefaultShardManagerBuilder.createLight
 */
inline fun LightShardManagerBuilder(
    token: String? = null,
    vararg intents: GatewayIntent,
    builder: InlineShardManagerBuilder.() -> Unit
                                   ) = InlineShardManagerBuilder(DefaultShardManagerBuilder.createLight(token, intents.toList()))
        .also(builder)

/**
 * Creates a new [ShardManager] instance with the default settings by using [DefaultShardManagerBuilder.create]
 *
 * @param token The token to log in with. If set to null, assign a value to [InlineShardManagerBuilder.token].
 * @param login Whether or not to automatically log in. See: [ShardManager.login]
 * @param intents A list of intents to enable.
 * @param builder The function used to configure the builder.
 * @return The new [ShardManager] instance.
 * @see DefaultShardManagerBuilder.create
 */
inline fun ShardManager(
    token: String? = null,
    login: Boolean = true,
    vararg intents: GatewayIntent,
    builder: InlineShardManagerBuilder.() -> Unit
                       ) = InlineShardManagerBuilder(DefaultShardManagerBuilder.create(token, intents.toList()))
        .also(builder)
        .build(login)

/**
 * Creates a new [ShardManager] instance with the default settings by using [DefaultShardManagerBuilder.createLight]
 *
 * @param token The token to log in with. If set to null, assign a value to [InlineShardManagerBuilder.token].
 * @param intents A list of intents to enable.
 * @param builder The function used to configure the builder.
 * @return The [ShardManager] instance. You must call [DefaultShardManagerBuilder.build] on this to finish constructing the ShardManager.
 * @see DefaultShardManagerBuilder.createLight
 */
inline fun ShardManagerBuilder(
    token: String? = null,
    vararg intents: GatewayIntent,
    builder: InlineShardManagerBuilder.() -> Unit
                              ) = InlineShardManagerBuilder(DefaultShardManagerBuilder.create(token, intents.toList()))
        .also(builder)

/**
 * Makes construction of a [DefaultShardManager] much more idiomatic and cleaner to do in kotlin.
 *
 * @property builder The delegate [DefaultShardManagerBuilder].
 * @constructor Create an inline shard manager builder to make construction of the [DefaultShardManager] more idiomatic in kotlin.
 */
class InlineShardManagerBuilder(val builder: DefaultShardManagerBuilder) {
    var rawEvents: Boolean = false
        set(value) {
            builder.setRawEventsEnabled(value)
            field = value
        }
    
    var relativeRateLimit: Boolean = true
        set(value) {
            builder.setRelativeRateLimit(value)
            field = value
        }
    
    var enableCache: Collection<CacheFlag> = emptySet()
        set(value) {
            builder.enableCache(value)
            field = value
        }
    
    var disableCache: Collection<CacheFlag> = emptySet()
        set(value) {
            builder.disableCache(value)
            field = value
        }
    
    var memberCachePolicy: MemberCachePolicy? = null
        set(value) {
            builder.setMemberCachePolicy(value)
            field = value
        }
    
    var sessionController: SessionController? = null
        set(value) {
            builder.setSessionController(value)
            field = value
        }
    
    var voiceDispatchInterceptor: VoiceDispatchInterceptor? = null
        set(value) {
            builder.setVoiceDispatchInterceptor(value)
            field = value
        }
    
    var contextMap: IntFunction<out ConcurrentMap<String, String>>? = null
        set(value) {
            builder.setContextMap(value)
            field = value
        }
    
    var contextEnabled: Boolean = true
        set(value) {
            builder.setContextEnabled(value)
            field = value
        }
    
    var compression: Compression? = null
        set(value) {
            if (value != null) {
                builder.setCompression(value)
            }
            field = value
        }
    
    var eventListeners: MutableCollection<Any> = DelegatingCollection(
            adder = { item -> builder.addEventListeners(item) },
            remover = { item -> builder.removeEventListeners(item) },
                                                                     )
    
    var eventListenerProvider: MutableCollection<IntFunction<Any>> = DelegatingCollection(
            adder = { item -> builder.addEventListenerProvider(item) },
            remover = { item -> builder.removeEventListenerProvider(item) },
                                                                                         )
    
    var audioSendFactory: IAudioSendFactory? = null
        set(value) {
            builder.setAudioSendFactory(value)
            field = value
        }
    
    var autoReconnect: Boolean = true
        set(value) {
            builder.setAutoReconnect(value)
            field = value
        }
    
    var bulkDeleteSplittingEnabled: Boolean = true
        set(value) {
            builder.setBulkDeleteSplittingEnabled(value)
            field = value
        }
    
    var enableShutdownHook: Boolean = true
        set(value) {
            builder.setEnableShutdownHook(value)
            field = value
        }
    
    @Deprecated("Use eventManagerProvider instead")
    var eventManager: IEventManager? = null
        set(value) {
            if (value != null) {
                builder.setEventManager(value)
            }
            field = value
        }
    
    var eventManagerProvider: IntFunction<out IEventManager>? = null
        set(value) {
            if (value != null) {
                builder.setEventManagerProvider(value)
            }
            field = value
        }
    
    var activity: Activity? = null
        set(value) {
            builder.setActivity(value)
            field = value
        }
    
    var activityProvider: IntFunction<Activity>? = null
        set(value) {
            builder.setActivityProvider(value)
            field = value
        }
    
    var idle: Boolean = false
        set(value) {
            builder.setIdle(value)
            field = value
        }
    
    var idleProvider: IntFunction<Boolean>? = null
        set(value) {
            builder.setIdleProvider(value)
            field = value
        }
    
    var status: OnlineStatus? = null
        set(value) {
            if (value != null) {
                builder.setStatus(value)
            }
            field = value
        }
    
    var statusProvider: IntFunction<OnlineStatus>? = null
        set(value) {
            if (value != null) {
                builder.setStatusProvider(value)
            }
            field = value
        }
    
    var threadFactory: ThreadFactory? = null
        set(value) {
            builder.setThreadFactory(value)
            field = value
        }
    
    var httpClientBuilder: OkHttpClient.Builder? = null
        set(value) {
            builder.setHttpClientBuilder(value)
            field = value
        }
    
    var httpClient: OkHttpClient? = null
        set(value) {
            builder.setHttpClient(value)
            field = value
        }
    
    var rateLimitPool: ScheduledExecutorService? = null
        set(value) {
            builder.setRateLimitPool(value, true)
            field = value
        }
    
    var rateLimitPoolProvider: ThreadPoolProvider<out ScheduledExecutorService>? = null
        set(value) {
            builder.setRateLimitPoolProvider(value)
            field = value
        }
    
    var gatewayPool: ScheduledExecutorService? = null
        set(value) {
            builder.setGatewayPool(value, true)
            field = value
        }
    
    var gatewayPoolProvider: ThreadPoolProvider<out ScheduledExecutorService>? = null
        set(value) {
            builder.setGatewayPoolProvider(value)
            field = value
        }
    
    var callbackPool: ExecutorService? = null
        set(value) {
            builder.setCallbackPool(value, true)
            field = value
        }
    
    var callbackPoolProvider: ThreadPoolProvider<out ExecutorService>? = null
        set(value) {
            builder.setCallbackPoolProvider(value)
            field = value
        }
    
    var eventPool: ExecutorService? = null
        set(value) {
            builder.setEventPool(value, true)
            field = value
        }
    
    var eventPoolProvider: ThreadPoolProvider<out ExecutorService>? = null
        set(value) {
            builder.setEventPoolProvider(value)
            field = value
        }
    
    var audioPool: ScheduledExecutorService? = null
        set(value) {
            builder.setAudioPool(value, true)
            field = value
        }
    
    var audioPoolProvider: ThreadPoolProvider<out ScheduledExecutorService>? = null
        set(value) {
            builder.setAudioPoolProvider(value)
            field = value
        }
    
    var maxReconnectDelay: Int = 900
        set(value) {
            builder.setMaxReconnectDelay(value)
            field = value
        }
    var requestTimeoutRetry: Boolean = true
        set(value) {
            builder.setRequestTimeoutRetry(value)
            field = value
        }
    
    var shards: Set<Int> = emptySet()
        set(value) {
            builder.setShards(shards)
            field = value
        }
    
    var shardsTotal: Int = -1
        set(value) {
            builder.setShardsTotal(value)
            field = value
        }
    
    
    fun shards(minShardId: Int = 0, maxShardId: Int) {
        shards = (minShardId..maxShardId).toSet()
    }
    
    fun shards(vararg shardIds: Int) {
        shards = shardIds.toSet()
    }
    
    var token: String? = null
        set(value) {
            if (value != null) {
                builder.setToken(value)
            }
            field = value
        }
    
    var useShutdownNow: Boolean = false
        set(value) {
            builder.setUseShutdownNow(value)
            field = value
        }
    
    var webSocketFactory: WebSocketFactory? = null
        set(value) {
            builder.setWebsocketFactory(value)
            field = value
        }
    
    var chunkingFilter: ChunkingFilter? = null
        set(value) {
            builder.setChunkingFilter(value)
            field = value
        }
    
    var enableIntents: Collection<GatewayIntent> = emptySet()
        set(value) {
            builder.enableIntents(value)
            field = value
        }
    
    var disableIntents: Collection<GatewayIntent> = emptySet()
        set(value) {
            builder.disableIntents(value)
            field = value
        }
    
    var largeThreshold: Int = 250
        set(value) {
            val coercedValue = value.coerceIn(50, 250)
            builder.setLargeThreshold(coercedValue)
            field = coercedValue
        }
    
    var maxBufferSize: Int = 2048
        set(value) {
            builder.setMaxBufferSize(value)
            field = value
        }
    
    var injectKtx: Boolean = true
    
    fun build(login: Boolean = false): ShardManager {
        if (injectKtx)
            builder.injectKTX()
        
        return builder.build(login)
    }
}