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


typealias StatusProvider = IntFunction<OnlineStatus>
typealias IdleProvider = IntFunction<Boolean>
typealias ActivityProvider = IntFunction<Activity>
typealias EventManagerProvider = IntFunction<out IEventManager>
typealias EventListenerProvider = IntFunction<Any>

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

    var context: Boolean = true
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

    val eventListeners: MutableCollection<Any> = DelegatingCollection(
            adder = { item -> builder.addEventListeners(item) },
            remover = { item -> builder.removeEventListeners(item) },
    )

    val eventListenerProviders: MutableCollection<IntFunction<Any>> = DelegatingCollection(
            adder = { item -> builder.addEventListenerProvider(item) },
            remover = { item -> builder.removeEventListenerProvider(item) },
    )

    /**
     * Sets an event listener on a per-shard basis..
     *
     * eg:
     * ```kotlin
     * eventListenerProvider {
     *     when(it) {
     *         1 -> ShardOneListener()
     *         2 -> ShardTwoListener()
     *         3 -> ShardThreeListener()
     *         else -> throw IllegalStateException("There are only 4 shards!")
     *     }
     * }
     * ```
     *
     * @param provider The provider for an event listener
     * @receiver The id of the shard
     */
    fun eventListenerProvider(provider: (Int) -> Any) {
        eventListenerProviders.add(EventListenerProvider(provider))
    }

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

    var bulkDeleteSplitting: Boolean = true
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
            if (value != null)
                eventManagerProvider = IntFunction { value }
            field = value
        }

    var eventManagerProvider: EventManagerProvider? = null
        set(value) {
            if (value != null) {
                builder.setEventManagerProvider(value)
            }
            field = value
        }

    /**
     * Set the event manager for each shard.
     *
     * eg:
     * ```kotlin
     * eventManagerProvider {
     *     InterfacedEventManager()
     * }
     * ```
     *
     * @param provider The provider for the event managers
     * @receiver The id of the shard
     */
    fun eventManagerProvider(provider: (Int) -> IEventManager) {
        eventManagerProvider = EventManagerProvider(provider)
    }

    var activity: Activity? = null
        set(value) {
            if (value != null)
                activityProvider = ActivityProvider { value }
            field = value
        }

    var activityProvider: ActivityProvider? = null
        set(value) {
            builder.setActivityProvider(value)
            field = value
        }

    /**
     * Set the activity on a per-shard basis.
     *
     * eg:
     * ```kotlin
     * activityProvider {
     *     Activity.playing("shard $it")
     * }
     * ```
     *
     * @param provider The provider for the bot activity
     * @receiver The id of the shard
     */
    fun activityProvider(provider: (Int) -> Activity) {
        activityProvider = ActivityProvider(provider)
    }

    var idle: Boolean = false
        set(value) {
            idleProvider { value }
            field = value
        }

    var idleProvider: IdleProvider? = null
        set(value) {
            builder.setIdleProvider(value)
            field = value
        }

    /**
     * Set whether or not the bot is idle on a per-shard basis.
     *
     * eg:
     * ```kotlin
     * idleProvider {
     *     if (it == 1) true
     *     else false
     * }
     * ```
     *
     * @param provider The provider for whether or not the shard is idle
     * @receiver The id of the shard
     */
    fun idleProvider(provider: (Int) -> Boolean) {
        idleProvider = IdleProvider(provider)
    }

    var status: OnlineStatus? = null
        set(value) {
            if (value != null) {
                statusProvider = StatusProvider { value }
            }
            field = value
        }

    var statusProvider: StatusProvider? = null
        set(value) {
            if (value != null) {
                builder.setStatusProvider(value)
            }
            field = value
        }

    /**
     * Sets the status on a per-shard basis.
     *
     * eg:
     * ```kotlin
     * statusProvider {
     *     if (it == 1) ONLINE
     *     else OFFLINE
     * }
     * ```
     *
     * @param provider The provider for the status
     * @receiver The id of the shard
     */
    fun statusProvider(provider: (Int) -> OnlineStatus) {
        statusProvider = StatusProvider(provider)
    }

    var threadFactory: ThreadFactory? = null
        set(value) {
            builder.setThreadFactory(value)
            field = value
        }

    /**
     * Set the thread factory to be used by internal executor of the [Shard Manager][ShardManager].
     *
     * ```kotlin
     * threadFactory {
     *     Thread(it)
     * }
     * ```
     *
     * @param factory The thread factory
     * @receiver The id of the shard
     */
    fun threadFactory(factory: (Runnable) -> Thread) {
        threadFactory = ThreadFactory(factory)
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

    /**
     * Set the [OkHttpClient Builder][OkHttpClient.Builder] via a receiver function on an [OkHttpClient Builder][OkHttpClient.Builder].
     * This **does not** invoke [OkHttpClient.Builder.build] and instead passes it to [DefaultShardManagerBuilder.setHttpClientBuilder].
     *
     * @param builder Configure the Builder receiver
     * @receiver The new [OkHttpClient Builder][OkHttpClient.Builder].
     * @see httpClient
     */
    fun httpClientBuilder(builder: OkHttpClient.Builder.() -> Unit) {
        val httpBuilder = OkHttpClient.Builder()
        httpBuilder.builder()
        httpClientBuilder = httpBuilder
    }

    /**
     * Set the [OkHttpClient] via a receiver function on an [OkHttpClient Builder][OkHttpClient.Builder].
     * This invokes [Builder.build][OkHttpClient.Builder.build] and passes it to [DefaultShardManagerBuilder.setHttpClient].
     *
     * @param builder Configure the Builder receiver
     * @receiver The new [OkHttpClient Builder][OkHttpClient.Builder].
     * @see httpClientBuilder
     */
    fun httpClient(builder: OkHttpClient.Builder.() -> Unit) {
        val httpBuilder = OkHttpClient.Builder()
        httpBuilder.builder()
        httpClient = httpBuilder.build()
    }

    /**
     * Set the [rate limit pool][DefaultShardManagerBuilder.setRateLimitPool] to be used for rate limit handling.
     *
     * ```kotlin
     * rateLimitPool(false, Executors.newScheduledThreadPool(4))
     * ```
     *
     * @param automaticShutdown Whether [JDA.shutdown()][net.dv8tion.jda.api.JDA.shutdown] should automatically shutdown this pool
     * @param pool The rate limit pool
     */
    fun rateLimitPool(automaticShutdown: Boolean = false, pool: ScheduledExecutorService) {
        builder.setRateLimitPool(pool, automaticShutdown)
    }

    /**
     * Set the [rate limit pool][DefaultShardManagerBuilder.setRateLimitPool] to be used for rate limit handling.
     *
     * ```kotlin
     * rateLimitPool(false) {
     *      Executors.newScheduledThreadPool(4)
     * }
     * ```
     *
     * @param automaticShutdown Whether [JDA.shutdown()][net.dv8tion.jda.api.JDA.shutdown] should automatically shutdown this pool
     * @param poolProvider The provider for the rate limit pool
     */
    fun rateLimitPool(automaticShutdown: Boolean = false, poolProvider: () -> ScheduledExecutorService) {
        builder.setRateLimitPool(poolProvider(), automaticShutdown)
    }

    var rateLimitPoolProvider: ThreadPoolProvider<out ScheduledExecutorService>? = null
        set(value) {
            builder.setRateLimitPoolProvider(value)
            field = value
        }

    /**
     * Set the [rate limit pool provider][DefaultShardManagerBuilder.setRateLimitPoolProvider] to be used for rate limit handling
     * on a per-shard basis.
     *
     * eg:
     * ```kotlin
     * rateLimitPoolProvider(true) {
     *     Executors.newScheduledThreadPool(4)
     * }
     * ```
     * @param automaticShutdown Whether [JDA.shutdown()][net.dv8tion.jda.api.JDA.shutdown] should automatically shutdown this pool
     * @param provider The provider for each shard's rate limit pool
     * @receiver The id of the shard
     */
    fun rateLimitPoolProvider(automaticShutdown: Boolean = false, provider: (Int) -> ScheduledExecutorService) {
        rateLimitPoolProvider = object : ThreadPoolProvider<ScheduledExecutorService> {
            override fun provide(shardId: Int): ScheduledExecutorService = provider(shardId)

            override fun shouldShutdownAutomatically(shardId: Int): Boolean = automaticShutdown
        }
    }

    /**
     * Set the [gateway pool][DefaultShardManagerBuilder.setGatewayPool] to be used for gateway updates.
     *
     * ```kotlin
     * gatewayPool(false, Executors.newScheduledThreadPool(4))
     * ```
     *
     * @param automaticShutdown Whether [JDA.shutdown()][net.dv8tion.jda.api.JDA.shutdown] should automatically shutdown this pool
     * @param pool The gateway pool
     */
    fun gatewayPool(automaticShutdown: Boolean = false, pool: ScheduledExecutorService) {
        builder.setGatewayPool(pool, automaticShutdown)
    }

    /**
     * Set the [gateway pool][DefaultShardManagerBuilder.setGatewayPool] to be used for gateway updates.
     *
     * ```kotlin
     * gatewayPool(false) {
     *      Executors.newScheduledThreadPool(4)
     * }
     * ```
     *
     * @param automaticShutdown Whether [JDA.shutdown()][net.dv8tion.jda.api.JDA.shutdown] should automatically shutdown this pool
     * @param poolProvider The provider for the gateway pool
     */
    fun gatewayPool(automaticShutdown: Boolean = false, poolProvider: () -> ScheduledExecutorService) {
        builder.setGatewayPool(poolProvider(), automaticShutdown)
    }

    var gatewayPoolProvider: ThreadPoolProvider<out ScheduledExecutorService>? = null
        set(value) {
            builder.setGatewayPoolProvider(value)
            field = value
        }

    /**
     * Set the [gateway pool provider][DefaultShardManagerBuilder.setGatewayPoolProvider] to be used for gateway updates.
     * on a per-shard basis.
     *
     * eg:
     * ```kotlin
     * gatewayPoolProvider(true) {
     *     Executors.newScheduledThreadPool(4)
     * }
     * ```
     * @param automaticShutdown Whether [JDA.shutdown()][net.dv8tion.jda.api.JDA.shutdown] should automatically shutdown this pool
     * @param provider The provider for each shard's gateway pool
     * @receiver The id of the shard
     */
    fun gatewayPoolProvider(automaticShutdown: Boolean = false, provider: (Int) -> ScheduledExecutorService) {
        gatewayPoolProvider = object : ThreadPoolProvider<ScheduledExecutorService> {
            override fun provide(shardId: Int): ScheduledExecutorService = provider(shardId)

            override fun shouldShutdownAutomatically(shardId: Int): Boolean = automaticShutdown
        }
    }

    /**
     * Set the [callback pool][DefaultShardManagerBuilder.setCallbackPool] to be used for callback handling.
     *
     * ```kotlin
     * callbackPool(false, Executors.newFixedThreadPool(4))
     * ```
     *
     * @param automaticShutdown Whether [JDA.shutdown()][net.dv8tion.jda.api.JDA.shutdown] should automatically shutdown this pool
     * @param pool The callback pool
     */
    fun callbackPool(automaticShutdown: Boolean = false, pool: ExecutorService) {
        builder.setCallbackPool(pool, automaticShutdown)
    }

    /**
     * Set the [callback pool][DefaultShardManagerBuilder.setCallbackPool] to be used for callback handling.
     *
     * ```kotlin
     * callbackPool(false) {
     *      Executors.newFixedThreadPool(4)
     * }
     * ```
     *
     * @param automaticShutdown Whether [JDA.shutdown()][net.dv8tion.jda.api.JDA.shutdown] should automatically shutdown this pool
     * @param poolProvider The provider for the callback pool
     */
    fun callbackPool(automaticShutdown: Boolean = false, poolProvider: () -> ExecutorService) {
        builder.setCallbackPool(poolProvider(), automaticShutdown)
    }

    var callbackPoolProvider: ThreadPoolProvider<out ExecutorService>? = null
        set(value) {
            builder.setCallbackPoolProvider(value)
            field = value
        }

    /**
     * Set the [callback pool provider][DefaultShardManagerBuilder.setCallbackPoolProvider] to be used for callback handling
     * on a per-shard basis.
     *
     * eg:
     * ```kotlin
     * callbackPoolProvider(true) {
     *     Executors.newFixedThreadPool(4)
     * }
     * ```
     * @param automaticShutdown Whether [JDA.shutdown()][net.dv8tion.jda.api.JDA.shutdown] should automatically shutdown this pool
     * @param provider The provider for each shard's callback poo
     * @receiver The id of the shardl
     */
    fun callbackPoolProvider(automaticShutdown: Boolean = false, provider: (Int) -> ExecutorService) {
        callbackPoolProvider = object : ThreadPoolProvider<ExecutorService> {
            override fun provide(shardId: Int): ExecutorService = provider(shardId)

            override fun shouldShutdownAutomatically(shardId: Int): Boolean = automaticShutdown
        }
    }

    /**
     * Set the [event pool][DefaultShardManagerBuilder.setEventPool] to be used for event handling.
     *
     * ```kotlin
     * eventPool(false, Executors.newFixedThreadPool(4))
     * ```
     *
     * @param automaticShutdown Whether [JDA.shutdown()][net.dv8tion.jda.api.JDA.shutdown] should automatically shutdown this pool
     * @param pool The event pool
     */
    fun eventPool(automaticShutdown: Boolean = false, pool: ExecutorService) {
        builder.setEventPool(pool, automaticShutdown)
    }

    /**
     * Set the [callback pool][DefaultShardManagerBuilder.setEventPool] to be used for event handling.
     *
     * ```kotlin
     * eventPool(false) {
     *      Executors.newFixedThreadPool(4)
     * }
     * ```
     *
     * @param automaticShutdown Whether [JDA.shutdown()][net.dv8tion.jda.api.JDA.shutdown] should automatically shutdown this pool
     * @param poolProvider The provider for the event pool
     */
    fun eventPool(automaticShutdown: Boolean = false, poolProvider: () -> ExecutorService) {
        builder.setEventPool(poolProvider(), automaticShutdown)
    }

    var eventPoolProvider: ThreadPoolProvider<out ExecutorService>? = null
        set(value) {
            builder.setEventPoolProvider(value)
            field = value
        }

    /**
     * Set the [event pool provider][DefaultShardManagerBuilder.setEventPoolProvider] to be used for event handling
     * on a per-shard basis.
     *
     * eg:
     * ```kotlin
     * callbackPoolProvider(true) {
     *     Executors.newFixedThreadPool(4)
     * }
     * ```
     * @param automaticShutdown Whether [JDA.shutdown()][net.dv8tion.jda.api.JDA.shutdown] should automatically shutdown this pool
     * @param provider The provider for each shard's event pool
     * @receiver The id of the shard
     */
    fun eventPoolProvider(automaticShutdown: Boolean = false, provider: (Int) -> ExecutorService) {
        eventPoolProvider = object : ThreadPoolProvider<ExecutorService> {
            override fun provide(shardId: Int): ExecutorService = provider(shardId)

            override fun shouldShutdownAutomatically(shardId: Int): Boolean = automaticShutdown
        }
    }

    /**
     * Set the [audio pool][DefaultShardManagerBuilder.setAudioPool] to be used for audio handling.
     *
     * ```kotlin
     * audioPool(false, Executors.newScheduledThreadPool(4))
     * ```
     *
     * @param automaticShutdown Whether [JDA.shutdown()][net.dv8tion.jda.api.JDA.shutdown] should automatically shutdown this pool
     * @param pool The audio pool
     */
    fun audioPool(automaticShutdown: Boolean = false, pool: ScheduledExecutorService) {
        builder.setGatewayPool(pool, automaticShutdown)
    }

    /**
     * Set the [audio pool][DefaultShardManagerBuilder.setAudioPool] to be used for audio handling.
     *
     * ```kotlin
     * audioPool(false) {
     *      Executors.newScheduledThreadPool(4)
     * }
     * ```
     *
     * @param automaticShutdown Whether [JDA.shutdown()][net.dv8tion.jda.api.JDA.shutdown] should automatically shutdown this pool
     * @param poolProvider The provider for the audio pool
     */
    fun audioPool(automaticShutdown: Boolean = false, poolProvider: () -> ScheduledExecutorService) {
        builder.setAudioPool(poolProvider(), automaticShutdown)
    }

    var audioPoolProvider: ThreadPoolProvider<out ScheduledExecutorService>? = null
        set(value) {
            builder.setAudioPoolProvider(value)
            field = value
        }

    /**
     * Set the [audio pool provider][DefaultShardManagerBuilder.setGatewayPoolProvider] to be used for audio handling.
     * on a per-shard basis.
     *
     * eg:
     * ```kotlin
     * audioPoolProvider(true) {
     *     Executors.newScheduledThreadPool(4)
     * }
     * ```
     * @param automaticShutdown Whether [JDA.shutdown()][net.dv8tion.jda.api.JDA.shutdown] should automatically shutdown this pool
     * @param provider The provider for each shard's audio pool
     * @receiver The id of the shard
     */
    fun audioPoolProvider(automaticShutdown: Boolean = false, provider: (Int) -> ScheduledExecutorService) {
        gatewayPoolProvider = object : ThreadPoolProvider<ScheduledExecutorService> {
            override fun provide(shardId: Int): ScheduledExecutorService = provider(shardId)

            override fun shouldShutdownAutomatically(shardId: Int): Boolean = automaticShutdown
        }
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

    /**
     * Sets the [chunking filter][DefaultShardManagerBuilder.setChunkingFilter] for all guilds.
     *
     * eg:
     * ```kotlin
     * chunkingFilter {
     *     return (it % 2) == 0L // only chunk guilds with an id % 2 = 0 (why tho)
     * }
     * ```
     *
     * @param filter The chunking filter
     */
    fun chunkingFilter(filter: (Long) -> Boolean) {
        chunkingFilter = ChunkingFilter(filter)
    }

    /**
     * Sets the [chunking filter][DefaultShardManagerBuilder.setChunkingFilter] for all guilds.
     *
     * @param ids The ids to filter for
     * @param include Whether to include or exclude these guilds for chunking
     * @see ChunkingFilter.include
     * @see ChunkingFilter.exclude
     */
    fun chunkingFilterByIds(include: Boolean = true, vararg ids: Long) {
        chunkingFilterByIds(include, ids.asList())
    }

    /**
     * Sets the [chunking filter][DefaultShardManagerBuilder.setChunkingFilter] for all guilds.
     *
     * @param ids The ids to filter for
     * @param include Whether to include or exclude these guilds for chunking
     * @see ChunkingFilter.include
     * @see ChunkingFilter.exclude
     */
    fun chunkingFilterByIds(include: Boolean = true, ids: Collection<Long>) {
        chunkingFilter {
            for (id in ids)
                return@chunkingFilter include
            return@chunkingFilter !include
        }
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
            builder.injectKTX(eventManagerProvider)

        return builder.build(login)
    }
}
