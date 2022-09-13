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

import net.dv8tion.jda.api.entities.*
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel
import net.dv8tion.jda.api.interactions.commands.CommandInteractionPayload
import net.dv8tion.jda.api.interactions.commands.OptionMapping
import net.dv8tion.jda.api.interactions.commands.OptionType


/**
 * Get an option value by name and resolve it to the desired type.
 * You can use the not-null assertion operator `!!` for required options which should never be null: `getOption<User>("user")!!`
 *
 * This is equivalent to `getOption(name) { it.getAsT() }`
 *
 *
 * This supports the following types:
 * - [User] and [Member] from [OptionType.USER]
 * - [Role] from [OptionType.ROLE]
 * - [Integer], [Int], and [Long] from [OptionType.INTEGER]
 * - [Double] from [OptionType.NUMBER]
 * - [Boolean] from [OptionType.BOOLEAN]
 * - [String] from [OptionType.STRING]
 * - [Message.Attachment] from [OptionType.ATTACHMENT]
 * - [IMentionable] from [OptionType.MENTIONABLE] (exclusive to [User] and [Role])
 * - Any [GuildChannel] type from [OptionType.CHANNEL]
 *
 * @throws[NoSuchElementException]
 *        If a type is unsupported
 *
 * @return The resolved option value as the given type, or null if the option is not provided by the user
 */
@Suppress("EXTENSION_SHADOWED_BY_MEMBER") // <- not true if you have generic parameters
inline fun <reified T> CommandInteractionPayload.getOption(name: String): T? = when(T::class.java) {
    User::class.java -> getOption(name, OptionMapping::getAsUser) as? T
    Member::class.java -> getOption(name, OptionMapping::getAsMember) as? T
    Role::class.java -> getOption(name, OptionMapping::getAsRole) as? T
    Integer::class.java, Int::class.java -> getOption(name, OptionMapping::getAsInt) as? T
    Long::class.java, java.lang.Long::class.java -> getOption(name, OptionMapping::getAsLong) as? T
    Double::class.java -> getOption(name, OptionMapping::getAsDouble) as? T
    Boolean::class.java, java.lang.Boolean::class.java -> getOption(name, OptionMapping::getAsBoolean) as? T
    String::class.java -> getOption(name, OptionMapping::getAsString) as? T
    Message.Attachment::class.java -> getOption(name, OptionMapping::getAsAttachment) as? T
    IMentionable::class.java -> getOption(name, OptionMapping::getAsMentionable) as? T
    else -> {
        if (GuildChannel::class.java.isAssignableFrom(T::class.java)) {
            val channel = getOption(name, OptionMapping::getAsChannel)
            when (channel) {
                is T? -> channel
                else -> throw NoSuchElementException("Cannot resolve channel of type ${T::class.java.simpleName}")
            }
        } else {
            throw NoSuchElementException("Type ${T::class.java.simpleName} is unsupported for getOption(name) resolution. Try updating or using a different type!")
        }
    }

}