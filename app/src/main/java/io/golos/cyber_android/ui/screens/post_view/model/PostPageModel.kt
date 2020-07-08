package io.golos.cyber_android.ui.screens.post_view.model

import androidx.lifecycle.LiveData
import io.golos.cyber_android.ui.dto.PostDonation
import io.golos.cyber_android.ui.screens.post_page_menu.model.PostMenu
import io.golos.cyber_android.ui.screens.post_view.dto.PostHeader
import io.golos.cyber_android.ui.screens.post_view.dto.SortingType
import io.golos.cyber_android.ui.shared.mvvm.model.ModelBase
import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem
import io.golos.domain.dto.*
import io.golos.domain.use_cases.community.SubscribeToCommunityUseCase
import io.golos.domain.use_cases.community.UnsubscribeToCommunityUseCase
import io.golos.domain.use_cases.model.CommentModel
import io.golos.domain.use_cases.model.DiscussionIdModel
import io.golos.domain.posts_parsing_rendering.post_metadata.post_dto.PostMetadata
import kotlinx.coroutines.flow.Flow
import java.io.File

interface PostPageModel : ModelBase, SubscribeToCommunityUseCase, UnsubscribeToCommunityUseCase {
    val postMetadata: PostMetadata

    val post: LiveData<List<VersionedListItem>>

    val commentsPageSize: Int

    val rewardCurrency: RewardCurrency

    val rewardCurrencyUpdates: Flow<RewardCurrency?>

    suspend fun loadPost()

    fun getPostMenu(): PostMenu

    fun getPostHeader(): PostHeader

    suspend fun getUserId(userNameOrId: String): UserIdDomain

    suspend fun addToFavorite(permlink: String)

    suspend fun removeFromFavorite(permlink: String)

    suspend fun deletePost(): String

    suspend fun upVote(
        communityId: CommunityIdDomain,
        userId: UserIdDomain,
        permlink: String
    )

    suspend fun downVote(
        communityId: CommunityIdDomain,
        userId: UserIdDomain,
        permlink: String
    )

    suspend fun reportPost(
        authorPostId: UserIdDomain,
        communityId: CommunityIdDomain,
        permlink: String,
        reason: String
    )

    suspend fun voteForComment(communityId: CommunityIdDomain, commentId: DiscussionIdModel, isUpVote: Boolean)

    suspend fun updateCommentsSorting(sortingType: SortingType)

    suspend fun loadStartFirstLevelCommentsPage()

    suspend fun loadNextFirstLevelCommentsPage()

    suspend fun retryLoadingFirstLevelCommentsPage()

    suspend fun loadNextSecondLevelCommentsPage(parentCommentId: DiscussionIdModel)

    suspend fun retryLoadingSecondLevelCommentsPage(parentCommentId: DiscussionIdModel)

    suspend fun sendComment(jsonBody: String)

    suspend fun deleteComment(commentId: DiscussionIdModel)

    fun getCommentText(commentId: DiscussionIdModel): List<CharSequence>

    fun getComment(commentId: ContentIdDomain): CommentModel?

    fun getComment(discussionIdModel: DiscussionIdModel): CommentModel?

    suspend fun updateComment(commentId: DiscussionIdModel, jsonBody: String)

    suspend fun replyToComment(repliedCommentId: DiscussionIdModel, jsonBody: String)

    suspend fun uploadAttachmentContent(file: File): String

    fun isTopReward(): Boolean?

    suspend fun getWalletBalance(): List<WalletCommunityBalanceRecordDomain>

    suspend fun updateDonation(donation: PostDonation)

    suspend fun updateRewardCurrency(currency: RewardCurrency)
}