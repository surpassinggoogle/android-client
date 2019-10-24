package io.golos.domain.interactors.community

import io.golos.domain.entities.CommunityDomain
import io.golos.domain.entities.CommunityPageDomain

interface CommunitiesRepository {

    suspend fun getCommunitiesByQuery(query: String?, offset: Int, pageLimitSize: Int): List<CommunityDomain>

    suspend fun getRecommendedCommunities(offset: Int, pageLimitSize: Int): List<CommunityDomain>

    suspend fun subscribeToCommunity(communityId: String)

    suspend fun unsubscribeToCommunity(communityId: String)

    suspend fun getCommunityPageById(communityId: String): CommunityPageDomain
}