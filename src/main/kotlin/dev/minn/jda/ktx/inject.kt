package dev.minn.jda.ktx

import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder

/**
 * Applies the [CoroutineEventManager] to this builder.
 */
fun JDABuilder.injectKTX() = setEventManager(CoroutineEventManager())

/**
 * Applies the [CoroutineEventManager] to this builder.
 */
fun DefaultShardManagerBuilder.injectKTX() = setEventManagerProvider { CoroutineEventManager() }