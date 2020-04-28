package dev.minn.jda.ktx

import kotlinx.coroutines.suspendCancellableCoroutine
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.requests.RestAction
import java.util.concurrent.CompletableFuture
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

suspend fun <T> RestAction<T>.await() = suspendCancellableCoroutine<T> { cont ->
    var check = true
    cont.invokeOnCancellation { check = false }
    setCheck { check }
    queue(cont::resume, cont::resumeWithException)
}

suspend fun <T> CompletableFuture<T>.await() = suspendCancellableCoroutine<T> {
    it.invokeOnCancellation { this.cancel(true) }
    whenComplete { r, e ->
        if (e != null)
            it.resumeWithException(e)
        else
            it.resume(r)
    }
}

suspend inline fun <reified T : GenericEvent> JDA.await(crossinline filter: (T) -> Boolean = { true }) = suspendCancellableCoroutine<T> {
    val listener = object : CoroutineEventListener {
        override suspend fun onEvent(event: GenericEvent) {
            if (event is T && filter(event)) {
                removeEventListener(this)
                it.resume(event)
            }
        }
    }.also(this::addEventListener)
    it.invokeOnCancellation { removeEventListener(listener) }
}