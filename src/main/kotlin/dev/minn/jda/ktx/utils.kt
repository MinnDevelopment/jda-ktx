/*
 * Copyright (c) 2021 Florian Spieß
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
 *
 */

package dev.minn.jda.ktx

import net.dv8tion.jda.api.utils.MemberCachePolicy

/**
 * Allows you to do
 * ```kotlin
 * memberCachePolicy = MemberCachePolicy.ONLINE or MemberCachePolicy.VOICE or MemberCachePolicy.OWNER
 * ```
 * instead of
 * ```kotlin
 * memberCachePolicy = MemberCachePolicy.ONLINE.or(MemberCachePolicy.VOICE).or(MemberCachePolicy.OWNER)
 * ```
 *
 * @param policy
 * @return
 */
infix fun MemberCachePolicy.or(policy: MemberCachePolicy): MemberCachePolicy {
    return policy.or(policy)
}

infix fun MemberCachePolicy.and(policy: MemberCachePolicy): MemberCachePolicy {
    return policy.and(policy)
}