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

package dev.minn.jda.ktx.interactions

import dev.minn.jda.ktx.events.onButton
import dev.minn.jda.ktx.jdabuilder.scope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.Emoji
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.interactions.components.buttons.Button
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle
import java.util.concurrent.ThreadLocalRandom
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes


/**
 * Defaults used for buttons.
 *
 * These can be changed but keep in mind they will apply globally.
 */
object ButtonDefaults {
    // Globally configurable defaults, I just chose defaults that I would use in my own bots

    /** The default expiration time of a button listener, in milliseconds. */
    var EXPIRATION: Duration = 15.minutes
    /** The default button style. */
    var STYLE: ButtonStyle = ButtonStyle.SECONDARY
    /** The default button labels */
    var LABEL: String? = null
    /** The default button emoji */
    var EMOJI: Emoji? = null
    /** The default button disabled state */
    var DISABLED: Boolean = false
}

/**
 * Create a button with keyword arguments.
 *
 * This will use the defaults from [ButtonDefaults] unless specified as parameters.
 *
 * @param[id] The component id to use.
 * @param[style] The button style.
 * @param[label] The button label
 * @param[emoji] The button emoji
 *
 * @return[Button] The resulting button instance.
 */
fun button(
    id: String,
    label: String? = ButtonDefaults.LABEL,
    emoji: Emoji? = ButtonDefaults.EMOJI,
    style: ButtonStyle = ButtonDefaults.STYLE,
    disabled: Boolean = ButtonDefaults.DISABLED,
) = Button.of(style, id, label, emoji).withDisabled(disabled)

/**
 * Create a button with keyword arguments.
 *
 * This is a shortcut for a button of style primary.
 *
 * This will use the defaults from [ButtonDefaults] unless specified as parameters.
 *
 * @param[id] The component id to use.
 * @param[label] The button label
 * @param[emoji] The button emoji
 *
 * @return[Button] The resulting button instance.
 */
fun primary(
    id: String,
    label: String? = ButtonDefaults.LABEL,
    emoji: Emoji? = ButtonDefaults.EMOJI,
    disabled: Boolean = ButtonDefaults.DISABLED,
) = button(id, label, emoji, ButtonStyle.PRIMARY, disabled)

/**
 * Create a button with keyword arguments.
 *
 * This is a shortcut for a button of style secondary.
 *
 * This will use the defaults from [ButtonDefaults] unless specified as parameters.
 *
 * @param[id] The component id to use.
 * @param[label] The button label
 * @param[emoji] The button emoji
 *
 * @return[Button] The resulting button instance.
 */
fun secondary(
    id: String,
    label: String? = ButtonDefaults.LABEL,
    emoji: Emoji? = ButtonDefaults.EMOJI,
    disabled: Boolean = ButtonDefaults.DISABLED,
) = button(id, label, emoji, ButtonStyle.SECONDARY, disabled)

/**
 * Create a button with keyword arguments.
 *
 * This is a shortcut for a button of style success.
 *
 * This will use the defaults from [ButtonDefaults] unless specified as parameters.
 *
 * @param[id] The component id to use.
 * @param[label] The button label
 * @param[emoji] The button emoji
 *
 * @return[Button] The resulting button instance.
 */
fun success(
    id: String,
    label: String? = ButtonDefaults.LABEL,
    emoji: Emoji? = ButtonDefaults.EMOJI,
    disabled: Boolean = ButtonDefaults.DISABLED,
) = button(id, label, emoji, ButtonStyle.SUCCESS, disabled)

/**
 * Create a button with keyword arguments.
 *
 * This is a shortcut for a button of style danger.
 *
 * This will use the defaults from [ButtonDefaults] unless specified as parameters.
 *
 * @param[id] The component id to use.
 * @param[label] The button label
 * @param[emoji] The button emoji
 *
 * @return[Button] The resulting button instance.
 */
fun danger(
    id: String,
    label: String? = ButtonDefaults.LABEL,
    emoji: Emoji? = ButtonDefaults.EMOJI,
    disabled: Boolean = ButtonDefaults.DISABLED,
) = button(id, label, emoji, ButtonStyle.DANGER, disabled)

/**
 * Create a button with keyword arguments.
 *
 * This is a shortcut for a button of style danger.
 *
 * This will use the defaults from [ButtonDefaults] unless specified as parameters.
 *
 * @param[id] The component id to use.
 * @param[label] The button label
 * @param[emoji] The button emoji
 *
 * @return[Button] The resulting button instance.
 */
fun link(
    url: String,
    label: String? = ButtonDefaults.LABEL,
    emoji: Emoji? = ButtonDefaults.EMOJI,
    disabled: Boolean = ButtonDefaults.DISABLED,
) = button(url, label, emoji, ButtonStyle.LINK, disabled)



/**
 * Create a button and apply a listener.
 *
 * This will use the defaults from [ButtonDefaults] unless specified as parameters.
 * You cannot configure the component ID, because it will be generated for you.
 *
 * @param[style] The button style.
 * @param[label] The button label
 * @param[emoji] The button emoji
 * @param[disabled] Whether the button is disabled
 * @param[expiration] The relative expiration time for the listener as [Duration], use [Duration.INFINITE] to disable timeout
 * @param[user] The user who is authorized to click the button. If the button is pressed by another user it will just defer edit and ignore.
 * @param[listener] The callback for the button clicks
 *
 * @return[Button] The resulting button instance. You still need to send it with a message!
 */
fun JDA.button(style: ButtonStyle = ButtonDefaults.STYLE, label: String? = ButtonDefaults.LABEL, emoji: Emoji? = ButtonDefaults.EMOJI,
               disabled: Boolean = ButtonDefaults.DISABLED, expiration: Duration = ButtonDefaults.EXPIRATION,
               user: User? = null, listener: suspend (ButtonInteractionEvent) -> Unit
): Button {
    val id = ThreadLocalRandom.current().nextLong().toString()
    val button = button(id, label, emoji, style, disabled)
    val task = onButton(id, timeout=expiration) {
        if (user == null || user == it.user)
            listener(it)
        // Always acknowledge regardless of user. You can add custom handling by just not using the user parameter and checking yourself
        if (!it.isAcknowledged)
            it.deferEdit().queue()
    }
    if (expiration.isPositive() && expiration.isFinite()) {
        scope.launch {
            delay(expiration)
            removeEventListener(task)
        }
    }
    return button
}