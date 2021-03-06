package io.golos.data.mappers

import io.golos.commun4j.services.model.GetProfileResult
import io.golos.domain.dto.UserIdDomain
import io.golos.domain.dto.UserProfileDomain

fun GetProfileResult.mapToUserProfileDomain(): UserProfileDomain {
    return UserProfileDomain(
        userId = UserIdDomain(userId.name),
        coverUrl = coverUrl,
        avatarUrl = avatarUrl,
        bio = personal?.biography,
        name = username!!,
        joinDate = registration!!.time,
        followersCount = subscribers?.usersCount ?: 0,
        followingsCount = subscriptions?.usersCount ?: 0,
        communitiesSubscribedCount = subscriptions?.communitiesCount ?: 0,
        highlightCommunities = highlightCommunities?.map { it.mapToCommunityDomain() } ?: listOf(),
        commonFriends = commonFriends?.map { it.mapToUserDomain() } ?: listOf(),
        isSubscribed = isSubscribed ?: false,
        isBlocked = isBlocked ?: false,
        isInBlacklist = isInBlacklist ?: false,
        isSubscription = isSubscription ?: false
    )
}