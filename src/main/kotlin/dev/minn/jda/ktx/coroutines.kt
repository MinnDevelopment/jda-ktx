package dev.minn.jda.ktx

import kotlinx.coroutines.suspendCancellableCoroutine
import net.dv8tion.jda.api.requests.RestAction
import net.dv8tion.jda.api.utils.concurrent.Task
import java.util.concurrent.CompletableFuture
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Awaits the result of this CompletableFuture
 *
 * @return Result
 */
suspend fun <T> CompletableFuture<T>.await() = suspendCancellableCoroutine<T> {
    it.invokeOnCancellation { cancel(true) }
    whenComplete { r, e ->
        when {
            e != null -> it.resumeWithException(e)
            else -> it.resume(r)
        }
    }
}

/**
 * Awaits the result of this RestAction
 *
 * @return Result
 */
@Suppress("HasPlatformType")
suspend fun <T> RestAction<T>.await() = submit().await()

/**
 * Awaits the result of this Task
 *
 * @return Result
 */
suspend fun <T> Task<T>.await() = suspendCancellableCoroutine<T> {
    it.invokeOnCancellation { cancel() }
    onSuccess { r -> it.resume(r)  }
    onError { e -> it.resumeWithException(e) }
}
