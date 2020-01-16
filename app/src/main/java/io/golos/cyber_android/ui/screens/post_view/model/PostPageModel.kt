package io.golos.cyber_android.ui.screens.post_view.model

import androidx.lifecycle.LiveData
import io.golos.cyber_android.ui.dto.ContentId
import io.golos.cyber_android.ui.screens.post_page_menu.model.PostMenu
import io.golos.cyber_android.ui.screens.post_view.dto.PostHeader
import io.golos.cyber_android.ui.screens.post_view.dto.SortingType
import io.golos.cyber_android.ui.shared.mvvm.model.ModelBase
import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem
import io.golos.domain.use_cases.community.SubscribeToCommunityUseCase
import io.golos.domain.use_cases.community.UnsubscribeToCommunityUseCase
import io.golos.domain.use_cases.model.DiscussionIdModel
import io.golos.domain.use_cases.post.post_dto.ContentBlock
import io.golos.domain.use_cases.post.post_dto.PostMetadata

interface PostPageModel : ModelBase, SubscribeToCommunityUseCase, UnsubscribeToCommunityUseCase {
    val postMetadata: PostMetadata

    val post: LiveData<List<VersionedListItem>>

    val commentsPageSize: Int

    suspend fun loadPost()

    fun getPostMenu(): PostMenu

    fun getPostHeader(): PostHeader

    suspend fun getUserId(userName: String): String

    suspend fun addToFavorite(permlink: String)

    suspend fun removeFromFavorite(permlink: String)

    suspend fun deletePost(): String

    suspend fun upVote(
        communityId: String,
        userId: String,
        permlink: String
    )

    suspend fun downVote(
        communityId: String,
        userId: String,
        permlink: String
    )

    suspend fun reportPost(
        authorPostId: String,
        communityId: String,
        permlink: String,
        reason: String
    )

    suspend fun voteForComment(commentId: DiscussionIdModel, isUpVote: Boolean)

    suspend fun updateCommentsSorting(sortingType: SortingType)

    suspend fun loadStartFirstLevelCommentsPage()

    suspend fun loadNextFirstLevelCommentsPage()

    suspend fun retryLoadingFirstLevelCommentsPage()

    suspend fun loadNextSecondLevelCommentsPage(parentCommentId: DiscussionIdModel)

    suspend fun retryLoadingSecondLevelCommentsPage(parentCommentId: DiscussionIdModel)

    suspend fun sendComment(commentText: String)

    suspend fun deleteComment(commentId: DiscussionIdModel)

    fun getCommentText(commentId: DiscussionIdModel): List<CharSequence>

    fun getCommentBody(commentId: ContentId): ContentBlock?

    suspend fun updateCommentText(commentId: DiscussionIdModel, newCommentText: String)

    suspend fun replyToComment(repliedCommentId: DiscussionIdModel, newCommentText: String)
}