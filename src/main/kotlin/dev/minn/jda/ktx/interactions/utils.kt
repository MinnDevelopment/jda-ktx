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
import net.dv8tion.jda.api.interactions.commands.OptionType

inline fun <reified T> optionType() = when(T::class.java) {
    java.lang.Float::class.java, java.lang.Double::class.java -> OptionType.NUMBER
    Integer::class.java, java.lang.Long::class.java, java.lang.Short::class.java, java.lang.Byte::class.java -> OptionType.INTEGER
    String::class.java -> OptionType.STRING
    User::class.java, Member::class.java -> OptionType.USER
    Role::class.java -> OptionType.ROLE
    java.lang.Boolean::class.java -> OptionType.BOOLEAN
    else -> when {
        Channel::class.java.isAssignableFrom(T::class.java) -> OptionType.CHANNEL
        IMentionable::class.java.isAssignableFrom(T::class.java) -> OptionType.MENTIONABLE
        else -> OptionType.UNKNOWN
    }
}
