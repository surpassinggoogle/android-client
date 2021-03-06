package io.golos.data.mappers

import io.golos.commun4j.services.model.GetCommunitiesItem
import io.golos.commun4j.sharedmodel.CyberName
import io.golos.domain.dto.CommunityDomain
import io.golos.domain.dto.CommunityIdDomain
import io.golos.domain.dto.CommunityPageDomain
import io.golos.domain.dto.CommunityRuleDomain
import java.util.*

fun GetCommunitiesItem.mapToCommunityDomain(): CommunityDomain =
    CommunityDomain(
        communityId = CommunityIdDomain(communityId),
        alias = alias,
        name = name,
        avatarUrl = avatarUrl,
        coverUrl = coverUrl,
        subscribersCount = subscribersCount,
        postsCount = postsCount ?: 0,
        isSubscribed = isSubscribed ?: false,
        isInBlacklist = isInBlacklist ?: false
    )

fun GetCommunitiesItem.mapToCommunityPageDomain(leaders: List<CyberName>,
    reportCount: Int, proposalCount: Int): CommunityPageDomain =
    CommunityPageDomain(
        communityId = CommunityIdDomain(communityId),
        name = name,
        avatarUrl = avatarUrl,
        coverUrl = coverUrl,
        description = description,
        rules = rules
            ?.filter { !it.text.isNullOrBlank() || !it.title.isNullOrBlank() }
            ?.map { CommunityRuleDomain(it.title, it.text) }
            ?: listOf() ,
        isSubscribed = isSubscribed ?: false,
        isBlocked = isBlocked ?: true,
        isInBlackList = isInBlacklist?:false,
        friendsCount = friendsCount ?: 0,
        friends = friends?.map { it.mapToCommunityFriendDomain(leaders) } ?: listOf(),
        membersCount = subscribersCount,
        leadsCount = leadersCount ?: 0,
        communityCurrency = CommunityPageDomain.CommunityPageCurrencyDomain(name, 1f),
        joinDate = registrationTime ?: Date(),
        alias = alias,
        isLeader = isLeader ?: false,
        postsCount = postsCount?:0,
        reportCount = reportCount,
        proposalCount = proposalCount
    )