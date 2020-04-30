package dev.minn.jda.ktx

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.suspendCancellableCoroutine
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.requests.RestAction
import net.dv8tion.jda.api.requests.restaction.pagination.PaginationAction
import net.dv8tion.jda.api.utils.concurrent.Task
import java.util.*
import java.util.concurrent.CancellationException
import java.util.concurrent.CompletableFuture
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

suspend fun <T> CompletableFuture<T>.await() = suspendCancellableCoroutine<T> {
    it.invokeOnCancellation { cancel(true) }
    whenComplete { r, e ->
        when {
            e != null -> it.resumeWithException(e)
            else -> it.resume(r)
        }
    }
}

@Suppress("HasPlatformType")
suspend fun <T> RestAction<T>.await() = submit().await()

@ExperimentalCoroutinesApi
suspend fun <T, M: PaginationAction<T, M>> M.produce(scope: CoroutineScope = GlobalScope) = scope.produce<T> {
    cache(false)
    val queue = LinkedList<T>()
    try {
        while (!isClosedForSend) {
            if (queue.isEmpty())
                queue.addAll(await())
            if (queue.isEmpty()) {
                close()
                break
            }

            while (!isClosedForSend && queue.isNotEmpty()) {
                send(queue.poll())
            }
        }
    } catch (ignored: CancellationException) {}
}

suspend fun <T> Task<T>.await() = suspendCancellableCoroutine<T> {
    it.invokeOnCancellation { cancel() }
    onSuccess { r -> it.resume(r)  }
    onError { e -> it.resumeWithException(e) }
}

suspend inline fun <reified T : GenericEvent> JDA.await(crossinline filter: (T) -> Boolean = { true }) = suspendCancellableCoroutine<T> {
    val listener = object : CoroutineEventListener {
        override suspend fun onEvent(event: GenericEvent) {
            if (event is T && filter(event)) {
                removeEventListener(this)
                it.resume(event)
            }
        }
    }
    addEventListener(listener)
    it.invokeOnCancellation { removeEventListener(listener) }
}