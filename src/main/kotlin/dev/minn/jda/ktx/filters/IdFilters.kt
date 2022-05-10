/*
 * Copyright (c) 2022. Human Ardaki
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

package dev.minn.jda.ktx.filters

import net.dv8tion.jda.api.events.message.GenericMessageEvent
import kotlin.jvm.Throws

//region GuildID
@Throws(KTXConstrainViolation::class)
infix fun <T : GenericMessageEvent> T.isFromGuildById(id: String): T =
    if (this.guild.id == id) this
    else throw KTXConstrainViolation("isFromGuildById assertion failed -> expected: $id , actual: ${this.guild.id}")

@Throws(KTXConstrainViolation::class)
infix fun <T : GenericMessageEvent> T.isNotFromGuildById(id: String): T {
    try {
        isFromGuildById(id)
    } catch (_: KTXConstrainViolation) {
        return this
    }
    throw KTXConstrainViolation("isNotFromGuildById assertion failed -> expected: !$id , actual: ${this.guild.id}")
}
// endregion

//region channelID
@Throws(KTXConstrainViolation::class)
infix fun <T : GenericMessageEvent> T.isFromChannelById(id: String): T =
    if (this.channel.id == id) this
    else throw KTXConstrainViolation("isFromChannelById assertion failed -> expected: $id , actual: ${this.channel.id}")

@Throws(KTXConstrainViolation::class)
infix fun <T : GenericMessageEvent> T.isNotFromChannelById(id: String): T {
    try {
        isFromChannelById(id)
    } catch (_: KTXConstrainViolation) {
        return this
    }
    throw KTXConstrainViolation("isNotFromChannelById assertion failed -> expected: !$id , actual: ${this.channel.id}")
}
// endregion

//region messageID
@Throws(KTXConstrainViolation::class)
infix fun <T : GenericMessageEvent> T.isMessageIdById(id: String): T =
    if (this.messageId == id) this
    else throw KTXConstrainViolation("isMessageIdById assertion failed -> expected: $id , actual: ${this.messageId}")

@Throws(KTXConstrainViolation::class)
infix fun <T : GenericMessageEvent> T.isNotMessageIdById(id: String): T {
    try {
        isMessageIdById(id)
    } catch (_: KTXConstrainViolation) {
        return this
    }
    throw KTXConstrainViolation("isNotMessageIdById assertion failed -> expected: !$id , actual: ${this.messageId}")
}
// endregion

// region memberID
@Throws(KTXConstrainViolation::class)
infix fun <T : GenericMessageEvent> T.isFromMemberById(id: String): T =
    if (getMember()?.id == id) this
    else throw KTXConstrainViolation("isFromMemberById assertion failed -> expected: $id , actual: ${getMember()?.id}")

@Throws(KTXConstrainViolation::class)
infix fun <T : GenericMessageEvent> T.isNotFromMemberById(id: String): T {
    try {
        isFromMemberById(id)
    } catch (_: KTXConstrainViolation) {
        return this
    }
    throw KTXConstrainViolation("isNotFromMemberById assertion failed -> expected: !$id , actual: ${getMember()?.id}")
}
// endregion