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