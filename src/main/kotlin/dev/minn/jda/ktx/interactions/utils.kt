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

import net.dv8tion.jda.api.entities.*
import net.dv8tion.jda.api.interactions.commands.CommandInteractionPayload
import net.dv8tion.jda.api.interactions.commands.OptionMapping
import net.dv8tion.jda.api.interactions.commands.OptionType
import java.io.File

inline fun <reified T> optionType() = when(T::class.java) {
    java.lang.Float::class.java, java.lang.Double::class.java -> OptionType.NUMBER
    Integer::class.java, java.lang.Long::class.java, java.lang.Short::class.java, java.lang.Byte::class.java -> OptionType.INTEGER
    String::class.java -> OptionType.STRING
    User::class.java, Member::class.java -> OptionType.USER
    Role::class.java -> OptionType.ROLE
    java.lang.Boolean::class.java -> OptionType.BOOLEAN
    File::class.java, Message.Attachment::class.java -> OptionType.ATTACHMENT
    else -> when {
        Channel::class.java.isAssignableFrom(T::class.java) -> OptionType.CHANNEL
        IMentionable::class.java.isAssignableFrom(T::class.java) -> OptionType.MENTIONABLE
        else -> OptionType.UNKNOWN
    }
}

@Suppress("EXTENSION_SHADOWED_BY_MEMBER") // <- not true if you have generic parameters
inline fun <reified T> CommandInteractionPayload.getOption(name: String): T? = when(T::class.java) {
    User::class.java -> getOption(name, OptionMapping::getAsUser) as? T
    Member::class.java -> getOption(name, OptionMapping::getAsMember) as? T
    Role::class.java -> getOption(name, OptionMapping::getAsRole) as? T
    Integer::class.java, Int::class.java -> getOption(name, OptionMapping::getAsInt) as? T
    Double::class.java -> getOption(name, OptionMapping::getAsDouble) as? T
    Long::class.java, java.lang.Long::class.java -> getOption(name, OptionMapping::getAsLong) as? T
    Boolean::class.java, java.lang.Boolean::class.java -> getOption(name, OptionMapping::getAsBoolean) as? T
    String::class.java -> getOption(name, OptionMapping::getAsString) as? T
    Message.Attachment::class.java -> getOption(name, OptionMapping::getAsAttachment) as? T
    IMentionable::class.java -> getOption(name, OptionMapping::getAsMentionable) as? T
    else -> {
        val channel = getOption(name, OptionMapping::getAsGuildChannel)
        if (channel is T)
            channel
        else
            throw NoSuchElementException("Cannot resolve options for type ${T::class.java.simpleName}")
    }
}