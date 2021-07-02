@file:Suppress("FunctionName", "unused", "MemberVisibilityCanBePrivate", "NOTHING_TO_INLINE")

package dev.minn.jda.ktx

import com.neovisionaries.ws.client.WebSocketFactory
import net.dv8tion.jda.api.GatewayEncoding
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.OnlineStatus
import net.dv8tion.jda.api.audio.factory.IAudioSendFactory
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.hooks.IEventManager
import net.dv8tion.jda.api.hooks.VoiceDispatchInterceptor
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.utils.ChunkingFilter
import net.dv8tion.jda.api.utils.Compression
import net.dv8tion.jda.api.utils.MemberCachePolicy
import net.dv8tion.jda.api.utils.SessionController
import net.dv8tion.jda.api.utils.cache.CacheFlag
import okhttp3.OkHttpClient
import java.util.*
import java.util.concurrent.ConcurrentMap
import java.util.concurrent.ExecutorService
import java.util.concurrent.ScheduledExecutorService

inline fun jda(token: String, jdaBuilder: JDABuilder.() -> Unit): JDA {
    val builder = JDABuilder.createDefault(token)
    builder.jdaBuilder()
    return builder.build()
}

inline fun DefaultJDA(
    token: String? = null,
    builder: InlineJDABuilder.() -> Unit
                     ) = InlineJDABuilder(JDABuilder.createDefault(token)).also(builder).build()

inline fun DefaultJDA(
    token: String? = null,
    intent: GatewayIntent,
    vararg intents: GatewayIntent,
    builder: InlineJDABuilder.() -> Unit
                     ) =
    InlineJDABuilder(JDABuilder.createDefault(token, intent, *intents)).also(builder).build()

inline fun DefaultJDA(
    token: String? = null,
    intents: Collection<GatewayIntent?>,
    builder: InlineJDABuilder.() -> Unit
                     ) =
    InlineJDABuilder(JDABuilder.createDefault(token, intents)).also(builder).build()

inline fun LightJDA(
    token: String? = null,
    builder: InlineJDABuilder.() -> Unit
                   ) = InlineJDABuilder(JDABuilder.createLight(token)).also(builder).build()

inline fun LightJDA(
    token: String? = null,
    intent: GatewayIntent,
    vararg intents: GatewayIntent,
    builder: InlineJDABuilder.() -> Unit
                   ) =
    InlineJDABuilder(JDABuilder.createLight(token, intent, *intents)).also(builder).build()

inline fun LightJDA(
    token: String? = null,
    intents: Collection<GatewayIntent?>,
    builder: InlineJDABuilder.() -> Unit
                   ) = InlineJDABuilder(JDABuilder.createLight(token, intents)).also(builder).build()

inline fun JDA(
    token: String? = null,
    intent: GatewayIntent,
    vararg intents: GatewayIntent,
    builder: InlineJDABuilder.() -> Unit
              ) = InlineJDABuilder(JDABuilder.create(token, intent, *intents)).also(builder).build()

inline fun JDA(
    token: String? = null,
    intents: Collection<GatewayIntent?>,
    builder: InlineJDABuilder.() -> Unit
              ) = InlineJDABuilder(JDABuilder.createLight(token, intents)).also(builder).build()

inline fun DefaultJDABuilder(
    token: String? = null,
    builder: InlineJDABuilder.() -> Unit
                            ) =
    InlineJDABuilder(JDABuilder.createDefault(token)).also(builder)

inline fun DefaultJDABuilder(
    token: String? = null,
    intent: GatewayIntent,
    vararg intents: GatewayIntent,
    builder: InlineJDABuilder.() -> Unit
                            ) =
    InlineJDABuilder(JDABuilder.createDefault(token, intent, *intents)).also(builder)

inline fun DefaultJDABuilder(
    token: String? = null,
    intents: Collection<GatewayIntent?>,
    builder: InlineJDABuilder.() -> Unit
                            ) =
    InlineJDABuilder(JDABuilder.createDefault(token, intents)).also(builder)

inline fun LightJDABuilder(
    token: String? = null,
    builder: InlineJDABuilder.() -> Unit
                          ) =
    InlineJDABuilder(JDABuilder.createLight(token)).also(builder)

inline fun LightJDABuilder(
    token: String? = null,
    intent: GatewayIntent,
    vararg intents: GatewayIntent,
    builder: InlineJDABuilder.() -> Unit
                          ) =
    InlineJDABuilder(JDABuilder.createLight(token, intent, *intents)).also(builder)

inline fun LightJDABuilder(
    token: String? = null,
    intents: Collection<GatewayIntent?>,
    builder: InlineJDABuilder.() -> Unit
                          ) =
    InlineJDABuilder(JDABuilder.createLight(token, intents)).also(builder)

inline fun JDABuilder(
    token: String? = null,
    intent: GatewayIntent,
    vararg intents: GatewayIntent,
    builder: InlineJDABuilder.() -> Unit
                     ) =
    InlineJDABuilder(JDABuilder.create(token, intent, *intents)).also(builder)

inline fun JDABuilder(
    token: String? = null,
    intents: Collection<GatewayIntent?>,
    builder: InlineJDABuilder.() -> Unit
                     ) =
    InlineJDABuilder(JDABuilder.createLight(token, intents)).also(builder)

class InlineJDABuilder(val builder: JDABuilder) {
    var gatewayEncoding: GatewayEncoding? = null
        set(value) {
            if (value != null) {
                builder.setGatewayEncoding(value)
            }
            field = value
        }

    var rawEventsEnabled: Boolean = false
        set(value) {
            builder.setRawEventsEnabled(value)
            field = value
        }

    var relativeRateLimit: Boolean = true
        set(value) {
            builder.setRelativeRateLimit(value)
            field = value
        }

    @Deprecated("We add CacheFlags to the enum over time which will be disabled when using this method. " +
                        "This introduces breaking changes due to the way the setter works. " +
                        "enableCache(flags) and disableCache(flags)")
    var cacheFlags: MutableSet<CacheFlag> = mutableSetOf()

    inline fun enableCache(cacheFlag: CacheFlag, vararg cacheFlags: CacheFlag) {
        builder.enableCache(cacheFlag, *cacheFlags)
    }

    inline fun enableCache(cacheFlags: Set<CacheFlag>) {
        builder.enableCache(cacheFlags)
    }

    inline fun disableCache(cacheFlag: CacheFlag, vararg cacheFlags: CacheFlag) {
        builder.disableCache(cacheFlag, *cacheFlags)
    }

    inline fun disableCache(cacheFlags: Set<CacheFlag>) {
        builder.disableCache(cacheFlags)
    }

    var memberCachePolicy: MemberCachePolicy? = null
        set(value) {
            builder.setMemberCachePolicy(value)
            field = value
        }

    var contextMap: ConcurrentMap<String, String>? = null
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

    var requestTimeoutRetry: Boolean = true
        set(value) {
            builder.setRequestTimeoutRetry(value)
            field = value
        }

    var token: String? = null
        set(value) {
            builder.setToken(value)
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

    var webSocketFactory: WebSocketFactory? = null
        set(value) {
            builder.setWebsocketFactory(value)
            field = value
        }

    var rateLimitPool: ScheduledExecutorService? = null
        set(value) {
            builder.setRateLimitPool(value, true)
            field = value
        }

    var gatewayPool: ScheduledExecutorService? = null
        set(value) {
            builder.setGatewayPool(value, true)
            field = value
        }

    var callbackPool: ExecutorService? = null
        set(value) {
            builder.setCallbackPool(value, true)
            field = value
        }

    var eventPool: ExecutorService? = null
        set(value) {
            builder.setEventPool(value, true)
            field = value
        }

    var audioPool: ScheduledExecutorService? = null
        set(value) {
            builder.setAudioPool(value, true)
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

    var autoReconnect: Boolean = true
        set(value) {
            builder.setAutoReconnect(value)
            field = value
        }

    var eventManager: IEventManager? = null
        set(value) {
            builder.setEventManager(value)
            field = value
        }

    var audioSendFactory: IAudioSendFactory? = null
        set(value) {
            builder.setAudioSendFactory(value)
            field = value
        }

    var idle: Boolean = false
        set(value) {
            builder.setIdle(value)
        }

    var activity: Activity? = null
        set(value) {
            builder.setActivity(value)
            field = value
        }

    var status: OnlineStatus? = null
        set(value) {
            if (value != null) {
                builder.setStatus(value)
            }
            field = value
        }

    var eventListeners: MutableSet<Any> = mutableSetOf()

    var maxReconnectDelay: Int = 900
        set(value) {
            builder.setMaxReconnectDelay(value)
            field = value
        }

    inline fun sharding(build: InlineSharding.() -> Unit) {
        val sharding = InlineSharding().also(build)
        builder.useSharding(sharding.shardId, sharding.shardTotal)
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

    var chunkingFilter: ChunkingFilter? = null
        set(value) {
            builder.setChunkingFilter(value)
            field = value
        }

    var gatewayIntents: MutableSet<GatewayIntent> = mutableSetOf()

    inline fun enableIntents(cacheFlag: GatewayIntent, vararg cacheFlags: GatewayIntent) {
        builder.enableIntents(cacheFlag, *cacheFlags)
    }

    inline fun enableIntents(cacheFlags: Set<GatewayIntent>) {
        builder.enableIntents(cacheFlags)
    }

    inline fun disableIntents(cacheFlag: GatewayIntent, vararg cacheFlags: GatewayIntent) {
        builder.disableIntents(cacheFlag, *cacheFlags)
    }

    inline fun disableIntents(cacheFlags: Set<GatewayIntent>) {
        builder.disableIntents(cacheFlags)
    }

    var largeThreshold: Int = 250
        set(value) {
            builder.setLargeThreshold(value)
            field = value
        }

    var maxBufferSize: Int = 2048
        set(value) {
            builder.setMaxBufferSize(value)
            field = value
        }

    var injectKtx: Boolean = true

    class InlineSharding {
        var shardId: Int = 0
        var shardTotal: Int = 0
    }

    fun build(): JDA {
        builder.addEventListeners(eventListeners)
        if (gatewayIntents.isNotEmpty())
            builder.setEnabledIntents(gatewayIntents)
        if (cacheFlags.isNotEmpty())
            builder.setEnabledCacheFlags(enumSetOf(*cacheFlags.toTypedArray()))
        if (injectKtx)
            builder.injectKTX()

        return builder.build()
    }
}

private inline fun <reified T : Enum<T>> enumSetOf(vararg elems: T) = EnumSet.noneOf(T::class.java).apply { addAll(elems) }