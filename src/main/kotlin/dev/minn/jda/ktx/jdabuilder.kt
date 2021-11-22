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
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.utils.cache.CacheFlag

inline fun light(token: String, enableCoroutines: Boolean = true, timeout: Long = -1, intent: GatewayIntent, vararg intents: GatewayIntent, builder: JDABuilder.() -> Unit = {})
    = JDABuilder.createLight(token, intent, *intents)
        .apply(builder)
        .apply {
            if (enableCoroutines)
                injectKTX(timeout=timeout)
        }
        .build()

inline fun light(token: String, enableCoroutines: Boolean = true, timeout: Long = -1, intents: Collection<GatewayIntent>, builder: JDABuilder.() -> Unit = {})
    = JDABuilder.createLight(token, intents)
        .apply(builder)
        .apply {
            if (enableCoroutines)
                injectKTX(timeout=timeout)
        }
        .build()

inline fun light(token: String, enableCoroutines: Boolean = true, timeout: Long = -1, builder: JDABuilder.() -> Unit = {})
    = JDABuilder.createLight(token)
        .apply(builder)
        .apply {
            if (enableCoroutines)
                injectKTX(timeout=timeout)
        }
        .build()




inline fun default(token: String, enableCoroutines: Boolean = true, timeout: Long = -1, intent: GatewayIntent, vararg intents: GatewayIntent, builder: JDABuilder.() -> Unit = {})
        = JDABuilder.createDefault(token, intent, *intents)
            .apply(builder)
            .apply {
                if (enableCoroutines)
                    injectKTX(timeout=timeout)
            }
            .build()

inline fun default(token: String, enableCoroutines: Boolean = true, timeout: Long = -1, intents: Collection<GatewayIntent>, builder: JDABuilder.() -> Unit = {})
        = JDABuilder.createDefault(token, intents)
            .apply(builder)
            .apply {
                if (enableCoroutines)
                    injectKTX(timeout=timeout)
            }
            .build()

inline fun default(token: String, enableCoroutines: Boolean = true, timeout: Long = -1, builder: JDABuilder.() -> Unit = {})
        = JDABuilder.createDefault(token)
            .apply(builder)
            .apply {
                if (enableCoroutines)
                    injectKTX(timeout=timeout)
            }
            .build()



inline fun createJDA(token: String, enableCoroutines: Boolean = true, timeout: Long = -1, intent: GatewayIntent, vararg intents: GatewayIntent, builder: JDABuilder.() -> Unit = {})
        = JDABuilder.create(token, intent, *intents)
            .apply(builder)
            .apply {
                if (enableCoroutines)
                    injectKTX(timeout=timeout)
            }
            .build()

inline fun createJDA(token: String, enableCoroutines: Boolean = true, timeout: Long = -1, intents: Collection<GatewayIntent>, builder: JDABuilder.() -> Unit = {})
        = JDABuilder.create(token, intents)
            .apply(builder)
            .apply {
                if (enableCoroutines)
                    injectKTX(timeout=timeout)
            }
            .build()



val JDABuilder.intents: IntentAccumulator get() = IntentAccumulator(this)
val JDABuilder.cache: CacheFlagAccumulator get() = CacheFlagAccumulator(this)

class IntentAccumulator(private val builder: JDABuilder) {
    operator fun plusAssign(intents: Collection<GatewayIntent>) {
        builder.enableIntents(intents)
    }

    operator fun minusAssign(intents: Collection<GatewayIntent>) {
        builder.disableIntents(intents)
    }
}

class CacheFlagAccumulator(private val builder: JDABuilder) {
    operator fun plusAssign(cacheFlags: Collection<CacheFlag>) {
        builder.enableCache(cacheFlags)
    }

    operator fun minusAssign(cacheFlags: Collection<CacheFlag>) {
        builder.disableCache(cacheFlags)
    }
}
