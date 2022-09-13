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

package dev.minn.jda.ktx.interactions.components

import dev.minn.jda.ktx.messages.MessageCreate
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel
import net.dv8tion.jda.api.entities.emoji.Emoji
import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.exceptions.ErrorHandler
import net.dv8tion.jda.api.hooks.EventListener
import net.dv8tion.jda.api.hooks.SubscribeEvent
import net.dv8tion.jda.api.interactions.InteractionHook
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback
import net.dv8tion.jda.api.interactions.components.ActionRow
import net.dv8tion.jda.api.interactions.components.buttons.Button
import net.dv8tion.jda.api.interactions.components.buttons.ButtonInteraction
import net.dv8tion.jda.api.requests.ErrorResponse
import net.dv8tion.jda.api.utils.messages.MessageCreateData
import net.dv8tion.jda.api.utils.messages.MessageEditData
import java.security.SecureRandom
import java.util.*
import kotlin.time.Duration

/**
 * Defaults used for paginators.
 *
 * These can be changed but keep in mind they will apply globally.
 */
object PaginatorDefaults {
    /** The default button to go to the previous page */
    var PREV: Button = Button.secondary("prev", Emoji.fromUnicode("⬅️"))
    /** The default button to go to the next page */
    var NEXT: Button = Button.secondary("next", Emoji.fromUnicode("➡️"))
    /** The default button to delete the paginator message */
    var DELETE: Button = Button.danger("delete", Emoji.fromUnicode("\uD83D\uDEAE"))
}

class Paginator internal constructor(private val nonce: String, private val ttl: Duration): EventListener {
    private var expiresAt: Long = Math.addExact(System.currentTimeMillis(), ttl.inWholeMilliseconds)

    private var index = 0
    private val pageCache = mutableListOf<MessageCreateData>()
    private val nextPage: MessageCreateData get() = pageCache[++index]
    private val prevPage: MessageCreateData get() = pageCache[--index]

    var filter: (ButtonInteraction) -> Boolean = { true }

    fun filterBy(filter: (ButtonInteraction) -> Boolean): Paginator {
        this.filter = filter
        return this
    }

    var prev: Button = PaginatorDefaults.PREV
    var next: Button = PaginatorDefaults.NEXT
    var delete: Button = PaginatorDefaults.DELETE

    internal val controls: ActionRow get() = ActionRow.of(
        prev.withDisabled(index == 0).withId("$nonce:prev"),
        next.withDisabled(index == pageCache.size - 1).withId("$nonce:next"),
        delete.withId("$nonce:delete")
    )

    val pages: List<MessageCreateData> get() = pageCache.toList()

    fun addPages(vararg page: MessageCreateData) {
        pageCache.addAll(page)
    }

    fun addPages(vararg page: MessageEmbed) {
        addPages(*page.map { MessageCreate(embeds=listOf(it)) }.toTypedArray())
    }

    @SubscribeEvent
    override fun onEvent(event: GenericEvent) {
        if (expiresAt < System.currentTimeMillis())
            return unregister(event.jda)
        if (event !is ButtonInteractionEvent) return
        val buttonId = event.componentId
        if (!buttonId.startsWith(nonce) || !filter(event)) return
        expiresAt = Math.addExact(System.currentTimeMillis(), ttl.inWholeMilliseconds)
        val (_, operation) = buttonId.split(":")
        when (operation) {
            "prev" -> {
                event.editMessage(MessageEditData.fromCreateData(prevPage))
                    .setComponents(controls)
                    .queue(null, ErrorHandler().handle(ErrorResponse.UNKNOWN_MESSAGE) { unregister(event.jda) })
            }
            "next" -> {
                event.editMessage(MessageEditData.fromCreateData(nextPage))
                    .setComponents(controls)
                    .queue(null, ErrorHandler().handle(ErrorResponse.UNKNOWN_MESSAGE) { unregister(event.jda) })
            }
            "delete" -> {
                unregister(event.jda)
                event.deferEdit().queue()
                event.hook.deleteOriginal().queue(null, ErrorHandler().ignore(ErrorResponse.UNKNOWN_MESSAGE))
            }
        }
    }

    private fun unregister(jda: JDA) {
        jda.removeEventListener(this)
    }
}

fun paginator(vararg pages: MessageCreateData, expireAfter: Duration): Paginator {
    val nonce = ByteArray(32)
    SecureRandom().nextBytes(nonce)
    return Paginator(Base64.getEncoder().encodeToString(nonce), expireAfter).also { it.addPages(*pages) }
}

fun paginator(vararg pages: MessageEmbed, expireAfter: Duration): Paginator
    = paginator(*pages.map { MessageCreate(embeds=listOf(it)) }.toTypedArray(), expireAfter=expireAfter)

fun MessageChannel.sendPaginator(paginator: Paginator)
    = sendMessage(paginator.also { jda.addEventListener(it) }.pages[0]).setComponents(paginator.controls)

fun InteractionHook.sendPaginator(paginator: Paginator)
    = sendMessage(paginator.also { jda.addEventListener(it) }.pages[0]).setComponents(paginator.controls)

fun IReplyCallback.replyPaginator(paginator: Paginator)
    = reply(paginator.also { user.jda.addEventListener(it) }.pages[0]).setComponents(paginator.controls)

fun MessageChannel.sendPaginator(
    vararg pages: MessageCreateData,
    expireAfter: Duration,
    filter: (ButtonInteraction) -> Boolean = {true}
) = sendPaginator(paginator(*pages, expireAfter=expireAfter).filterBy(filter))
fun MessageChannel.sendPaginator(
    vararg pages: MessageEmbed,
    expireAfter: Duration,
    filter: (ButtonInteraction) -> Boolean = {true}
) = sendPaginator(paginator(*pages, expireAfter=expireAfter).filterBy(filter))

fun InteractionHook.sendPaginator(
    vararg pages: MessageCreateData,
    expireAfter: Duration,
    filter: (ButtonInteraction) -> Boolean = {true}
) = sendPaginator(paginator(*pages, expireAfter=expireAfter).filterBy(filter))
fun InteractionHook.sendPaginator(
    vararg pages: MessageEmbed,
    expireAfter: Duration,
    filter: (ButtonInteraction) -> Boolean = {true}
) = sendPaginator(paginator(*pages, expireAfter=expireAfter).filterBy(filter))

fun IReplyCallback.replyPaginator(
    vararg pages: MessageCreateData,
    expireAfter: Duration,
    filter: (ButtonInteraction) -> Boolean = {true}
) = replyPaginator(paginator(*pages, expireAfter=expireAfter).filterBy(filter))
fun IReplyCallback.replyPaginator(
    vararg pages: MessageEmbed,
    expireAfter: Duration,
    filter: (ButtonInteraction) -> Boolean = {true}
) = replyPaginator(paginator(*pages, expireAfter=expireAfter).filterBy(filter))