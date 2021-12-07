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

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ClosedSendChannelException
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import net.dv8tion.jda.api.requests.restaction.pagination.PaginationAction
import java.util.*

/**
 * Converts this PaginationAction to a [Flow]
 *
 * This is the same as
 * ```kotlin
 * flow {
 *   emitAll(produce())
 * }
 * ```
 *
 * @return[Flow] instance
 */
fun <T, M: PaginationAction<T, M>> M.asFlow(): Flow<T> = flow {
    cache(false)
    val queue = LinkedList<T>(await())
    while (queue.isNotEmpty()) {
        while (queue.isNotEmpty()) {
            emit(queue.poll())
        }
        queue.addAll(await())
    }
}

/**
 * Converts this PaginationAction to a [ReceiveChannel].
 *
 * Remember to close the channel, or close the coroutine scope, once you don't need it anymore.
 *
 * @return [ReceiveChannel] instance
 */
suspend fun <T, M: PaginationAction<T, M>> M.produce(): ReceiveChannel<T> = coroutineScope {
    val channel = Channel<T>(1) // we don't need a buffer
    cache(false)
    launch {
        channel.runCatching {
            val queue = LinkedList<T>(await())
            while (queue.isNotEmpty()) {
                while (queue.isNotEmpty()) {
                    send(queue.poll())
                }
                queue.addAll(await())
            }
        }.onFailure {
            if (it !is ClosedSendChannelException && it !is CancellationException)
                throw it
        }
    }

    channel
}
