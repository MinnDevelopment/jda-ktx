package dev.minn.jda.ktx

import kotlinx.coroutines.suspendCancellableCoroutine
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.MessageChannel
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

suspend inline fun MessageChannel.awaitMessage(
    author: User? = null, // filter by user
    crossinline filter: (Message) -> Boolean = { true } // filter by filter (lol)
): Message {
    return jda.await<MessageReceivedEvent> {
        it.channel == this
            && (author == null || it.author == author)
            && filter(it.message)
    }.message
}
