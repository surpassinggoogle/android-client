package io.golos.cyber_android.ui.common.posts

import androidx.lifecycle.LiveData
import io.golos.cyber_android.ui.common.AbstractFeedViewModel
import io.golos.cyber_android.utils.asEvent
import io.golos.domain.entities.DiscussionEntity
import io.golos.domain.interactors.action.VoteUseCase
import io.golos.domain.interactors.feed.AbstractFeedUseCase
import io.golos.domain.interactors.model.CommentCreationRequestModel
import io.golos.domain.interactors.model.DiscussionIdModel
import io.golos.domain.interactors.model.DiscussionModel
import io.golos.domain.interactors.publish.DiscussionPosterUseCase
import io.golos.domain.model.FeedUpdateRequest

/**
 * Extends [AbstractFeedViewModel] with ability to create comments via [sendComment] method. Result of creation
 * can be listened by [discussionCreationLiveData]
 */
abstract class AbstractFeedWithCommentsViewModel<out R : FeedUpdateRequest, E: DiscussionEntity, M: DiscussionModel>(
    feedUseCase: AbstractFeedUseCase<out R, E, M>,
    voteUseCase: VoteUseCase,
    private val posterUseCase: DiscussionPosterUseCase
) :
    AbstractFeedViewModel<R, E, M>(feedUseCase, voteUseCase) {

    /**
     * [LiveData] for post creation process
     */
    val discussionCreationLiveData = posterUseCase.getAsLiveData.asEvent()

    fun sendComment(discussion: DiscussionModel, comment: String) {
        sendComment(discussion.contentId, comment)
    }

    fun sendComment(id: DiscussionIdModel, comment: String) {
        if (validate(comment)) {
            val postRequest = CommentCreationRequestModel(comment, id, emptyList())
            posterUseCase.createPostOrComment(postRequest)
        }
    }

    private fun validate(comment: String) = comment.length > 3

    init {
        posterUseCase.subscribe()
    }

    override fun onCleared() {
        super.onCleared()
        posterUseCase.unsubscribe()
    }
}