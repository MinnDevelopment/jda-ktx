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

package dev.minn.jda.ktx.interactions.commands

import net.dv8tion.jda.api.entities.*
import net.dv8tion.jda.api.entities.channel.Channel
import net.dv8tion.jda.api.interactions.commands.OptionType
import java.io.File

inline fun <reified T> optionType() = when(T::class.java) {
    Float::class.java, Double::class.java -> OptionType.NUMBER
    Int::class.java, Long::class.java, Short::class.java, Byte::class.java -> OptionType.INTEGER
    String::class.java -> OptionType.STRING
    User::class.java, Member::class.java -> OptionType.USER
    Role::class.java -> OptionType.ROLE
    Boolean::class.java -> OptionType.BOOLEAN
    File::class.java, Message.Attachment::class.java -> OptionType.ATTACHMENT
    else -> when {
        Channel::class.java.isAssignableFrom(T::class.java) -> OptionType.CHANNEL
        IMentionable::class.java.isAssignableFrom(T::class.java) -> OptionType.MENTIONABLE
        else -> OptionType.UNKNOWN
    }
}
