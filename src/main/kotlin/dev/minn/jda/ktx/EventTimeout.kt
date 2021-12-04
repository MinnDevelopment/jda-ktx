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

import dev.minn.jda.ktx.EventTimeout.Inherit
import dev.minn.jda.ktx.EventTimeout.Limit
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

/**
 * Utility class to allow for inherited or custom timeouts.
 *
 * @see [Inherit]
 * @see [Limit]
 * @see [Long.toTimeout]
 */
sealed class EventTimeout(val time: Duration) {
    /**
     * Inherit the timeout from the event manager
     */
    object Inherit : EventTimeout(Duration.ZERO)

    /**
     * Set a custom timeout in milliseconds
     */
    class Limit(time: Duration) : EventTimeout(time)
}

/**
 * Convert this long to [EventTimeout.Limit] or [EventTimeout.Inherit] on null
 */
fun Long?.toTimeout() = this?.milliseconds?.toTimeout()

/**
 * Convert this long to [EventTimeout.Limit] or [EventTimeout.Inherit] on null
 */
fun Duration?.toTimeout() = this?.let { EventTimeout.Limit(it) } ?: EventTimeout.Inherit
