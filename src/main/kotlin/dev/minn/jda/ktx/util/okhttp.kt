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

package dev.minn.jda.ktx.util

import kotlinx.coroutines.suspendCancellableCoroutine
import net.dv8tion.jda.api.exceptions.HttpException
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Process and await the result of a [Call].
 *
 * This does not handle any HTTP error codes, since it might be of interest to handle them in the handler.
 *
 * The [Response] is closed immediately after the [handler] returns.
 *
 * @param[T] The type of the result.
 * @param[throwOnStatus] Whether to throw an [HttpException] if the status code is not 2xx.
 * @param[handler] The result handler, which should process the response fully.
 *
 * @return The result of the [handler].
 */
suspend inline fun <T> Call.awaitWith(throwOnStatus: Boolean = false, crossinline handler: (Response) -> T): T = suspendCancellableCoroutine { sink ->
    sink.invokeOnCancellation { cancel() }
    enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            sink.resumeWithException(e)
        }

        override fun onResponse(call: Call, response: Response) {
            if (throwOnStatus && !response.isSuccessful)
                throw HttpException("${response.code} ${response.message}")
            response.use {
                runCatching {
                    handler(response)
                }.let(sink::resumeWith)
            }
        }
    })
}

/**
 * Awaits the [Response] of the [Call].
 *
 * It is the responsibility of the caller to handle HTTP error codes and to close the [Response].
 *
 * @return The [Response].
 */
suspend fun Call.await(): Response = suspendCancellableCoroutine { sink ->
    sink.invokeOnCancellation { cancel() }
    enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            sink.resumeWithException(e)
        }

        override fun onResponse(call: Call, response: Response) {
             sink.resume(response)
        }
    })
}