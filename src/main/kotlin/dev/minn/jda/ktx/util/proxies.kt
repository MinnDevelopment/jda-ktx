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

package dev.minn.jda.ktx.util

import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.Role
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.reflect.KProperty

open class BackedReference<T>(private var entity: T, private val update: (T) -> T?) {
    operator fun getValue(thisRef: Any?, prop: KProperty<*>): T {
        entity = update(entity) ?: entity
        return entity
    }
}

fun User.ref() = BackedReference(this) {
    this.jda.getUserById(this.idLong)
}

fun Member.ref() = BackedReference(this) {
    guild.getMemberById(idLong)
}

fun Guild.ref() = BackedReference(this) {
    jda.getGuildById(idLong)
}

fun Role.ref() = BackedReference(this) {
    guild.getRoleById(idLong)
}

fun PrivateChannel.ref() = BackedReference(this) {
    jda.getPrivateChannelById(idLong)
}

@Suppress("UNCHECKED_CAST")
fun <T : GuildChannel> T.ref() = BackedReference(this) {
    jda.getGuildChannelById(type, idLong) as T
}

object SLF4J {
    operator fun getValue(thisRef: Any?, prop: KProperty<*>): Logger {
        return LoggerFactory.getLogger(thisRef!!::class.java)!!
    }

    /**
     * Shortcut for [LoggerFactory.getLogger] with string name
     *
     * @param name
     *        The name of the logger
     */
    operator fun invoke(name: String) = lazy {
        LoggerFactory.getLogger(name)!!
    }

    // for some reason you can't link specific overloads...
    /**
     * Shortcut for [LoggerFactory.getLogger] with class parameter
     *
     * @param[T]
     *       The type the logger is for
     */
    inline operator fun <reified T> invoke() = lazy {
        LoggerFactory.getLogger(T::class.java)!!
    }
}

// Example Usage:

/*

class ModLog(channel: TextChannel) {
    private val channel: TextChannel by channel.ref()

    fun onBan(target: User, moderator: Member, reason: String? = null) {
        channel.sendMessage(EmbedBuilder().run {
            setColor(0xa83636)
            setAuthor(moderator.user.asTag, null, moderator.user.avatarUrl)
            setDescription("Banned %#s (%s)".format(target, target.id))
            setTimestamp(Instant.now())
            if (reason != null)
                appendDescription("\n Reason: ").appendDescription(reason)
            build()
        }).queue()
    }
}

class GuildEnvironment(val prefix: String, guild: Guild, modLog: Long) {
    val guild: Guild by guild.ref()
    val modLog = ModLog(guild.getTextChannelById(modLog)!!)
}

 */
