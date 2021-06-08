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

import net.dv8tion.jda.api.entities.AbstractChannel
import net.dv8tion.jda.api.entities.IMentionable
import net.dv8tion.jda.api.entities.Role
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.interactions.commands.OptionType

inline fun <reified T> optionType() = when(T::class.java) {
    Int::class.java -> OptionType.INTEGER
    String::class.java -> OptionType.STRING
    User::class.java -> OptionType.USER
    Role::class.java -> OptionType.ROLE
    Boolean::class.java -> OptionType.BOOLEAN
    else -> when {
        AbstractChannel::class.java.isAssignableFrom(T::class.java) -> OptionType.CHANNEL
        IMentionable::class.java.isAssignableFrom(T::class.java) -> OptionType.MENTIONABLE
        else -> OptionType.UNKNOWN
    }
}

data class Shape(val rows: Int, val cols: Int) {
    fun check(row: Int, col: Int) {
        check(row in 0 until rows) { "Row index $row is not in range [0, $rows)" }
        check(col in 0 until cols) { "Column index $col is not in range [0, $cols)" }
    }
}