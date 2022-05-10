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

// region having role
@Throws(KTXConstrainViolation::class)
infix fun <T : GenericMessageEvent> T.memberHasAnyRoleByName(roleName: String): T {
    return if (
        getMember()
            ?.roles
            ?.any { role -> role.name == roleName } == true
    ) this
    else throw KTXConstrainViolation("memberHasAnyRoleByName assertion failed -> expected: $roleName")
}

@Throws(KTXConstrainViolation::class)
infix fun <T : GenericMessageEvent> T.memberHasNoRoleByName(roleName: String): T {
    try {
        memberHasAnyRoleByName(roleName)
    } catch (_: KTXConstrainViolation) {
        return this
    }
    throw KTXConstrainViolation("memberHasNoRoleByName assertion failed -> expected: !$roleName")
}
// endregion