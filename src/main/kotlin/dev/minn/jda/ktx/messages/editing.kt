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

package dev.minn.jda.ktx.messages

import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel
import net.dv8tion.jda.api.interactions.InteractionHook
import net.dv8tion.jda.api.interactions.callbacks.IMessageEditCallback
import net.dv8tion.jda.api.interactions.components.LayoutComponent
import net.dv8tion.jda.api.requests.restaction.WebhookMessageEditAction
import net.dv8tion.jda.api.requests.restaction.interactions.MessageEditCallbackAction
import net.dv8tion.jda.api.utils.AttachedFile

/**
 * Defaults used for edit message extensions provided by this module.
 * Each function that relies on these defaults, says so explicitly. This does not apply for send functions.
 *
 * These can be changed but keep in mind they will apply globally.
 */
object MessageEditDefaults {
    /**
     * Whether message edits should replace the entire message by default
     */
    var replace: Boolean = false
}

// Using an underscore at the end to prevent overload specialization
// You can remove it with an import alias (import ... as foo) but i would recommend against it

/**
 * Edit the original message from this interaction.
 *
 * This makes use of [MessageEditDefaults].
 *
 * This does not currently replace files but may do so in the future.
 *
 * @param[content] The message content
 * @param[embeds] Multiple embeds
 * @param[components] Components for this message
 * @param[attachments] Multiple files
 * @param[replace] Whether this should replace the entire message
 *
 * @return[MessageEditCallbackAction]
 */
fun IMessageEditCallback.editMessage_(
    content: String? = null,
    embeds: Collection<MessageEmbed>? = null,
    components: Collection<LayoutComponent>? = null,
    attachments: Collection<AttachedFile>? = null,
    replace: Boolean = MessageEditDefaults.replace
): MessageEditCallbackAction = deferEdit().applyData(MessageEdit(content, embeds, attachments, components, null, replace))

/**
 * Edit a message from this interaction.
 *
 * This makes use of [MessageEditDefaults].
 *
 * This does not currently replace files but may do so in the future.
 *
 * @param[id] The message id, defaults to "@original"
 * @param[content] The message content
 * @param[embeds] Multiple embeds
 * @param[components] Components for this message
 * @param[attachments] Multiple files
 * @param[replace] Whether this should replace the entire message
 *
 * @return[WebhookMessageEditAction]
 */
fun InteractionHook.editMessage(
    id: String = "@original",
    content: String? = null,
    embeds: Collection<MessageEmbed>? = null,
    components: Collection<LayoutComponent>? = null,
    attachments: Collection<AttachedFile>? = null,
    replace: Boolean = MessageEditDefaults.replace,
) = editMessageById(id, "tmp").applyData(MessageEdit(content, embeds, attachments, components, null, replace))

/**
 * Edit a message from this channel.
 *
 * This makes use of [MessageEditDefaults].
 *
 * This does not currently replace files but may do so in the future.
 *
 * @param[id] The message id
 * @param[content] The message content
 * @param[embeds] Multiple embeds
 * @param[components] Components for this message
 * @param[attachments] Multiple files
 * @param[replace] Whether this should replace the entire message
 *
 * @return[net.dv8tion.jda.api.requests.restaction.MessageEditAction]
 */
fun MessageChannel.editMessage(
    id: String,
    content: String? = null,
    embeds: Collection<MessageEmbed>? = null,
    components: Collection<LayoutComponent>? = null,
    attachments: Collection<AttachedFile>? = null,
    replace: Boolean = MessageEditDefaults.replace,
) = editMessageById(id, MessageEdit(content, embeds, attachments, components, null, replace))

/**
 * Edit the message.
 *
 * This makes use of [MessageEditDefaults].
 *
 * This does not currently replace files but may do so in the future.
 *
 * @param[content] The message content
 * @param[embeds] Multiple embeds
 * @param[components] Components for this message
 * @param[attachments] Multiple files
 * @param[replace] Whether this should replace the entire message
 *
 * @return[net.dv8tion.jda.api.requests.restaction.MessageEditAction]
 */
fun Message.edit(
    content: String? = null,
    embeds: Collection<MessageEmbed>? = null,
    components: Collection<LayoutComponent>? = null,
    attachments: Collection<AttachedFile>? = null,
    replace: Boolean = MessageEditDefaults.replace,
) = channel.editMessage(id, content, embeds, components, attachments, replace)