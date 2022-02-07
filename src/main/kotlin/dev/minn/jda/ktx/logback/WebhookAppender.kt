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

package dev.minn.jda.ktx.logback

import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.classic.filter.ThresholdFilter
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.AppenderBase
import club.minnced.discord.webhook.WebhookClient
import club.minnced.discord.webhook.WebhookClientBuilder
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread

open class WebhookAppender : AppenderBase<ILoggingEvent>() {
    companion object {
        private val guard = ThreadLocal.withInitial { false }
    }

    var timeout: String = "30000"
    var level: String = "warn"
    lateinit var encoder: PatternLayoutEncoder // use xml config to set pattern
    lateinit var url: String // use xml config to set url

    private lateinit var client: WebhookClient
    private lateinit var pool: ScheduledExecutorService

    private val buffer = StringBuilder(2000)

    private fun flush() = synchronized(buffer) {
        if (buffer.isEmpty()) return@synchronized
        val message = "```ansi\n${buffer}```"
        buffer.setLength(0)
        client.send(message).exceptionally { null }
    }

    override fun append(event: ILoggingEvent) {
        if (guard.get()) return
        val msg = encoder.encode(event).toString(Charsets.UTF_8)
        synchronized(buffer) {
            msg.lineSequence().filter { it.isNotBlank() }.forEach { line ->
                if (buffer.length + line.length > 1900)
                    flush()
                buffer.append(line).append("\n")
            }
        }
    }

    override fun start() {
        if (!::encoder.isInitialized)
            throw AssertionError("Missing pattern encoder for webhook appender")
        if (!::url.isInitialized)
            throw AssertionError("Missing url for webhook appender")

        // Set level threshold to prevent spam
        addFilter(ThresholdFilter().apply {
            setLevel(level)
            start()
        })

        pool = Executors.newSingleThreadScheduledExecutor {
            thread(start = false, isDaemon = true, name = "WebhookAppender") {
                guard.set(true)
                it.run()
            }
        }

        client = WebhookClientBuilder(url)
            .setWait(false)
            .setExecutorService(pool)
            .build()
            .setTimeout(timeout.toLong())

        pool.scheduleAtFixedRate(this::flush, 500, 500, TimeUnit.MILLISECONDS)
        encoder.start()
        super.start()
    }

    override fun stop() {
        if (::pool.isInitialized)
            pool.shutdown()
        super.stop()
    }
}