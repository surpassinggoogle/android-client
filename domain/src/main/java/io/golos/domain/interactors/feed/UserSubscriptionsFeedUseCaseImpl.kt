package io.golos.domain.interactors.feed

import io.golos.domain.repositories.DiscussionsFeedRepository
import io.golos.domain.DispatchersProvider
import io.golos.domain.repositories.Repository
import io.golos.domain.entities.CyberUser
import io.golos.domain.entities.DiscussionsSort
import io.golos.domain.entities.PostEntity
import io.golos.domain.entities.VoteRequestEntity
import io.golos.domain.interactors.model.FeedTimeFrameOption
import io.golos.domain.interactors.model.PostModel
import io.golos.domain.interactors.model.UpdateOption
import io.golos.domain.mappers.PostFeedEntityToModelMapper
import io.golos.domain.requestmodel.PostFeedUpdateRequest
import io.golos.domain.requestmodel.UserSubscriptionsFeedUpdateRequest
import javax.inject.Inject

interface UserSubscriptionsFeedUseCase {
    val baseFeedUpdateRequest: UserSubscriptionsFeedUpdateRequest

    fun requestFeedUpdate(limit: Int, option: UpdateOption, sort: DiscussionsSort?, timeFrame: FeedTimeFrameOption?)
}

class UserSubscriptionsFeedUseCaseImpl
@Inject
constructor(
    private val userId: CyberUser,
    postFeedRepository: DiscussionsFeedRepository<PostEntity, PostFeedUpdateRequest>,
    voteRepository: Repository<VoteRequestEntity, VoteRequestEntity>,
    feedMapper: PostFeedEntityToModelMapper,
    dispatchersProvider: DispatchersProvider
) : AbstractFeedUseCase<PostFeedUpdateRequest, PostEntity, PostModel>(
    postFeedRepository,
    voteRepository,
    feedMapper,
    dispatchersProvider
), UserSubscriptionsFeedUseCase {
    override val baseFeedUpdateRequest: UserSubscriptionsFeedUpdateRequest
        get() = UserSubscriptionsFeedUpdateRequest(userId.userId, 0, DiscussionsSort.FROM_NEW_TO_OLD, FeedTimeFrameOption.ALL, null)

    override fun requestFeedUpdate(limit: Int, option: UpdateOption,
                                   sort: DiscussionsSort?, timeFrame: FeedTimeFrameOption?) {
        discussionsFeedRepository.makeAction(
            UserSubscriptionsFeedUpdateRequest(
                userId.userId,
                limit,
                DiscussionsSort.FROM_NEW_TO_OLD,
                timeFrame ?: FeedTimeFrameOption.ALL,
                when (option.resolveUpdateOption()) {
                    UpdateOption.REFRESH_FROM_BEGINNING -> null
                    UpdateOption.FETCH_NEXT_PAGE -> discussionsFeedRepository.getAsLiveData(baseFeedUpdateRequest).value?.nextPageId
                }
            )
        )
    }
}