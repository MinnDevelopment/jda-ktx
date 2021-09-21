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

import dev.minn.jda.ktx.onButton
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.Emoji
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent
import net.dv8tion.jda.api.interactions.components.Button
import net.dv8tion.jda.api.interactions.components.ButtonStyle
import java.util.concurrent.ThreadLocalRandom
import java.util.concurrent.TimeUnit


/**
 * Defaults used for buttons.
 *
 * These can be changed but keep in mind they will apply globally.
 */
object ButtonDefaults {
    // Globally configurable defaults, I just chose defaults that I would use in my own bots

    /** The default expiration time of a button listener, in milliseconds. */
    var EXPIRATION: Long = TimeUnit.MINUTES.toMillis(15)
    /** The default button style. */
    var STYLE: ButtonStyle = ButtonStyle.SECONDARY
    /** The default button labels */
    var LABEL: String? = null
    /** The default button emoji */
    var EMOJI: Emoji? = null
}


/**
 * Create a button and apply a listener.
 *
 * This will use the defaults from [ButtonDefaults] unless specified as parameters.
 * You cannot configure the component ID, because it will be generated for you.
 *
 * @param[style] The button style.
 * @param[label] The button label
 * @param[emoji] The button emoji
 * @param[expiration] The relative expiration time for the listener in milliseconds
 * @param[user] The user who is authorized to click the button. If the button is pressed by another user it will just defer edit and ignore.
 * @param[listener] The callback for the button clicks
 *
 * @return[Button] The resulting button instance. You still need to send it with a message!
 */
suspend fun JDA.button(style: ButtonStyle = ButtonDefaults.STYLE, label: String? = ButtonDefaults.LABEL, emoji: Emoji? = ButtonDefaults.EMOJI,
                       expiration: Long = ButtonDefaults.EXPIRATION, user: User? = null, listener: suspend (ButtonClickEvent) -> Unit
): Button {
    val id = ThreadLocalRandom.current().nextLong().toString()
    val button = Button.of(style, id, label, emoji)
    val task = onButton(id) {
        if (user == null || user == it.user)
            listener(it)
        // Always acknowledge regardless of user. You can add custom handling by just not using the user parameter and checking yourself
        if (!it.isAcknowledged)
            it.deferEdit().queue()
    }
    if (expiration > 0) {
        GlobalScope.launch {
            delay(expiration)
            removeEventListener(task)
        }
    }
    return button
}