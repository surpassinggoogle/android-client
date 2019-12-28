package io.golos.cyber_android.ui.screens.community_page_post.view_model

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.common.mvvm.viewModel.ViewModelBase
import io.golos.cyber_android.ui.common.mvvm.view_commands.*
import io.golos.cyber_android.ui.common.paginator.Paginator
import io.golos.cyber_android.ui.dto.ContentId
import io.golos.cyber_android.ui.dto.Post
import io.golos.cyber_android.ui.mappers.mapToPostsList
import io.golos.cyber_android.ui.screens.community_page_post.model.CommunityPostModel
import io.golos.cyber_android.ui.screens.community_page_post.view.*
import io.golos.cyber_android.ui.mappers.mapToTimeFrameDomain
import io.golos.cyber_android.ui.mappers.mapToTypeFeedDomain
import io.golos.cyber_android.ui.screens.community_page.child_pages.community_post.model.TimeConfigurationDomain
import io.golos.cyber_android.ui.screens.my_feed.view_model.MyFeedListListener
import io.golos.cyber_android.ui.screens.post_filters.PostFiltersHolder
import io.golos.cyber_android.ui.screens.post_page_menu.model.PostMenu
import io.golos.cyber_android.ui.screens.post_report.view.PostReportDialog
import io.golos.cyber_android.ui.utils.PAGINATION_PAGE_SIZE
import io.golos.cyber_android.ui.utils.toLiveData
import io.golos.domain.DispatchersProvider
import io.golos.domain.commun_entities.Permlink
import io.golos.domain.dependency_injection.Clarification
import io.golos.domain.dto.PostsConfigurationDomain
import io.golos.domain.repositories.CurrentUserRepositoryRead
import io.golos.domain.use_cases.model.DiscussionIdModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.json.JSONArray
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

class CommunityPostViewModel @Inject constructor(
    dispatchersProvider: DispatchersProvider,
    model: CommunityPostModel,
    private val currentUserRepository: CurrentUserRepositoryRead,
    @Named(Clarification.COMMUNITY_ID) private val communityId: String,
    private val paginator: Paginator.Store<Post>
) : ViewModelBase<CommunityPostModel>(dispatchersProvider, model), MyFeedListListener {

    private val _postsListState: MutableLiveData<Paginator.State> = MutableLiveData(Paginator.State.Empty)
    val postsListState = _postsListState.toLiveData()

    private val _filterUpdateTime: MutableLiveData<TimeConfigurationDomain> =
        MutableLiveData(
            TimeConfigurationDomain(
                PostFiltersHolder.UpdateTimeFilter.POPULAR,
                PostFiltersHolder.PeriodTimeFilter.PAST_24_HOURS
            )
        )
    val filterPostState = _filterUpdateTime.toLiveData()

    private var loadPostsJob: Job? = null
    private lateinit var postsConfigurationDomain: PostsConfigurationDomain

    init {
        paginator.sideEffectListener = {
            when (it) {
                is Paginator.SideEffect.LoadPage -> loadMorePosts(it.pageCount)
                is Paginator.SideEffect.ErrorEvent -> {

                }
            }
        }
        paginator.render = {
            _postsListState.value = it
        }

        updateFilterAndLoadPosts()
    }

    fun loadFilter() {
        val configuration = _filterUpdateTime.value
        configuration?.let { config ->
            _command.value = NavigateToFilterDialogViewCommand(config)
        }
    }

    private fun updateFilterAndLoadPosts() {
        val timeConfiguration = _filterUpdateTime.value
        timeConfiguration?.let { config ->
            postsConfigurationDomain = PostsConfigurationDomain(
                currentUserRepository.userId.userId,
                communityId,
                null,
                PostsConfigurationDomain.SortByDomain.TIME_DESC,
                config.periodFilter.mapToTimeFrameDomain(),
                PAGINATION_PAGE_SIZE,
                0,
                config.timeFilter.mapToTypeFeedDomain()
            )
            restartLoadPosts()
        }
    }

    fun updatePostsByFilter(
        filterTime: PostFiltersHolder.UpdateTimeFilter,
        periodTime: PostFiltersHolder.PeriodTimeFilter
    ) {
        _filterUpdateTime.value = TimeConfigurationDomain(filterTime, periodTime)
        updateFilterAndLoadPosts()
    }

    override fun onMenuClicked(postMenu: PostMenu) {
        _command.value = NavigationToPostMenuViewCommand(postMenu)
    }

    override fun onLinkClicked(linkUri: Uri) {
        _command.value = NavigateToLinkViewCommand(linkUri)
    }

    override fun onImageClicked(imageUri: Uri) {
        _command.value = NavigateToImageViewCommand(imageUri)
    }

    override fun onSeeMoreClicked(contentId: ContentId) {
        val discussionIdModel = DiscussionIdModel(contentId.userId, Permlink(contentId.permlink))
        _command.value = NavigateToPostCommand(discussionIdModel, contentId)
    }

    override fun onItemClicked(contentId: ContentId) {
        val discussionIdModel = DiscussionIdModel(contentId.userId, Permlink(contentId.permlink))
        _command.value = NavigateToPostCommand(discussionIdModel, contentId)
    }

    override fun onUserClicked(userId: String) {
        if (currentUserRepository.userId.userId != userId) {
            _command.value = NavigateToUserProfileViewCommand(userId)
        }
    }

    override fun onCommentsClicked(postContentId: ContentId) {
        val discussionIdModel = DiscussionIdModel(postContentId.userId, Permlink(postContentId.permlink))
        _command.value = NavigateToPostCommand(discussionIdModel, postContentId)
    }

    override fun onShareClicked(shareUrl: String) {
        _command.value = SharePostCommand(shareUrl)
    }

    override fun onUpVoteClicked(contentId: ContentId) {
        launch {
            try {
                _command.value = SetLoadingVisibilityCommand(true)
                model.upVote(contentId.communityId, contentId.userId, contentId.permlink)
                _postsListState.value = updateUpVoteCountOfVotes(_postsListState.value, contentId)
            } catch (e: java.lang.Exception) {
                Timber.e(e)
                _command.value = ShowMessageResCommand(R.string.unknown_error)
            } finally {
                _command.value = SetLoadingVisibilityCommand(false)
            }
        }
    }

    override fun onDownVoteClicked(contentId: ContentId) {
        launch {
            try {
                _command.value = SetLoadingVisibilityCommand(true)
                model.downVote(contentId.communityId, contentId.userId, contentId.permlink)
                _postsListState.value = updateDownVoteCountOfVotes(_postsListState.value, contentId)
            } catch (e: java.lang.Exception) {
                Timber.e(e)
                _command.value = ShowMessageResCommand(R.string.unknown_error)
            } finally {
                _command.value = SetLoadingVisibilityCommand(false)
            }
        }
    }

    fun loadInitialPosts() {
        val postsListState = _postsListState.value
        if (postsListState is Paginator.State.Empty || postsListState is Paginator.State.EmptyError) {
            restartLoadPosts()
        }
    }

    fun loadMorePosts() {
        paginator.proceed(Paginator.Action.LoadMore)
    }

    fun addToFavorite(permlink: String) {
        launch {
            try {
                _command.value = SetLoadingVisibilityCommand(true)
                model.addToFavorite(permlink)
            } catch (e: java.lang.Exception) {
                Timber.e(e)
            } finally {
                _command.value = SetLoadingVisibilityCommand(false)
            }
        }
    }

    fun removeFromFavorite(permlink: String) {
        launch {
            try {
                _command.value = SetLoadingVisibilityCommand(true)
                model.removeFromFavorite(permlink)
            } catch (e: java.lang.Exception) {
                Timber.e(e)
            } finally {
                _command.value = SetLoadingVisibilityCommand(false)
            }
        }
    }

    fun editPost(permlink: String) {
        val post = getPostFromPostsListState(permlink)
        post?.let {
            _command.value = EditPostCommand(it)
        }
    }

    fun deletePost(permlink: String, communityId: String) {
        launch {
            try {
                _command.value = SetLoadingVisibilityCommand(true)
                model.deletePost(permlink, communityId)
                _postsListState.value = deletePostInState(_postsListState.value, permlink)
            } catch (e: java.lang.Exception) {
                Timber.e(e)
            } finally {
                _command.value = SetLoadingVisibilityCommand(false)
            }
        }
    }

    fun deleteLocalPostByPermlink(permlink: String) {
        _postsListState.value = deletePostInState(_postsListState.value, permlink)
    }

    fun subscribeToCommunity(communityId: String) {
        launch {
            try {
                _command.value = SetLoadingVisibilityCommand(true)
                model.subscribeToCommunity(communityId)
                _postsListState.value = changeCommunitySubscriptionStatusInState(_postsListState.value, communityId, true)
            } catch (e: java.lang.Exception) {
                Timber.e(e)
            } finally {
                _command.value = SetLoadingVisibilityCommand(false)
            }
        }
    }

    fun unsubscribeToCommunity(communityId: String) {
        launch {
            try {
                _command.value = SetLoadingVisibilityCommand(true)
                model.unsubscribeToCommunity(communityId)
                _postsListState.value = changeCommunitySubscriptionStatusInState(_postsListState.value, communityId, false)
            } catch (e: java.lang.Exception) {
                Timber.e(e)
            } finally {
                _command.value = SetLoadingVisibilityCommand(false)
            }
        }
    }

    fun onReportPostClicked(permlink: String) {
        val post = getPostFromPostsListState(permlink)
        post?.let { _command.value = ReportPostCommand(post) }
    }

    fun sendReport(report: PostReportDialog.Report) {
        launch {
            try {
                _command.value = SetLoadingVisibilityCommand(true)
                val collectedReports = report.reasons
                val reason = JSONArray(collectedReports).toString()
                model.reportPost(
                    authorPostId = report.contentId.userId,
                    communityId = report.contentId.communityId,
                    permlink = report.contentId.permlink,
                    reason = reason
                )
            } catch (e: Exception) {
                Timber.e(e)
                _command.value = ShowMessageResCommand(R.string.common_general_error)
            } finally {
                _command.value = SetLoadingVisibilityCommand(false)
            }
        }
    }

    private fun loadMorePosts(pageCount: Int) {
        loadPostsJob = launch {
            try {
                postsConfigurationDomain = postsConfigurationDomain.copy(offset = pageCount * PAGINATION_PAGE_SIZE)
                val postsDomainList = model.getPosts(postsConfigurationDomain)
                val postList = postsDomainList.mapToPostsList()
                launch(Dispatchers.Main) {
                    paginator.proceed(
                        Paginator.Action.NewPage(
                            pageCount,
                            postList
                        )
                    )
                }
            } catch (e: Exception) {
                Timber.e(e)
                paginator.proceed(Paginator.Action.PageError(e))
            }
        }
    }

    private fun restartLoadPosts() {
        loadPostsJob?.cancel()
        paginator.proceed(Paginator.Action.Restart)
    }

    private fun getPostFromPostsListState(permlink: String): Post? {
        when (postsListState.value) {
            is Paginator.State.Data<*> -> {
                return ((postsListState.value as Paginator.State.Data<*>).data as List<Post>)
                    .find { post ->
                        post.contentId.permlink == permlink
                    }
            }
            is Paginator.State.Refresh<*> -> {
                return ((postsListState.value as Paginator.State.Refresh<*>).data as List<Post>)
                    .find { post ->
                        post.contentId.permlink == permlink
                    }
            }
            is Paginator.State.NewPageProgress<*> -> {
                return ((postsListState.value as Paginator.State.NewPageProgress<*>).data as List<Post>)
                    .find { post ->
                        post.contentId.permlink == permlink
                    }
            }
            is Paginator.State.FullData<*> -> {
                return ((postsListState.value as Paginator.State.FullData<*>).data as List<Post>)
                    .find { post ->
                        post.contentId.permlink == permlink
                    }
            }
        }
        return null
    }

    private fun deletePostInState(state: Paginator.State?, permlink: String): Paginator.State? {
        when (state) {
            is Paginator.State.Data<*> -> {
                deletePostByPermlink(((state).data as ArrayList<Post>), permlink)
            }
            is Paginator.State.Refresh<*> -> {
                deletePostByPermlink(((state).data as ArrayList<Post>), permlink)
            }
            is Paginator.State.NewPageProgress<*> -> {
                deletePostByPermlink(((state).data as ArrayList<Post>), permlink)
            }
            is Paginator.State.FullData<*> -> {
                deletePostByPermlink(((state).data as ArrayList<Post>), permlink)
            }
        }
        return state
    }

    private fun deletePostByPermlink(posts: ArrayList<Post>, permlink: String) {
        val foundedPost = posts.find { post -> post.contentId.permlink == permlink }?.copy()
        foundedPost?.let { post -> posts.remove(post) }
    }

    private fun changeCommunitySubscriptionStatusInState(
        state: Paginator.State?,
        communityId: String,
        isSubscribed: Boolean
    ): Paginator.State? {
        when (state) {
            is Paginator.State.Data<*> -> {
                val post = (state as? List<Post>)?.find { it.community.communityId == communityId }
                post?.community?.isSubscribed = isSubscribed
            }
            is Paginator.State.Refresh<*> -> {
                val post = (state as? List<Post>)?.find { it.community.communityId == communityId }
                post?.community?.isSubscribed = isSubscribed

            }
            is Paginator.State.NewPageProgress<*> -> {
                val post = (state as? List<Post>)?.find { it.community.communityId == communityId }
                post?.community?.isSubscribed = isSubscribed

            }
            is Paginator.State.FullData<*> -> {
                val post = (state as? List<Post>)?.find { it.community.communityId == communityId }
                post?.community?.isSubscribed = isSubscribed
            }
        }
        return state
    }

    private fun updateUpVoteCountOfVotes(
        state: Paginator.State?,
        contentId: ContentId
    ): Paginator.State? {
        when (state) {
            is Paginator.State.Data<*> -> {
                updateUpVoteInPostsByContentId(((state).data as ArrayList<Post>), contentId)

            }
            is Paginator.State.Refresh<*> -> {
                updateUpVoteInPostsByContentId(((state).data as ArrayList<Post>), contentId)

            }
            is Paginator.State.NewPageProgress<*> -> {
                updateUpVoteInPostsByContentId(((state).data as ArrayList<Post>), contentId)
            }
            is Paginator.State.FullData<*> -> {
                updateUpVoteInPostsByContentId(((state).data as ArrayList<Post>), contentId)
            }
        }
        return state
    }

    private fun updateUpVoteInPostsByContentId(posts: ArrayList<Post>, contentId: ContentId) {
        val foundedPost = posts.find { post ->
            post.contentId == contentId
        }
        val updatedPost = foundedPost?.copy()
        updatedPost?.let { post ->
            if (!post.votes.hasUpVote) {
                val oldVotes = post.votes
                post.votes = post.votes.copy(
                    upCount = post.votes.upCount + 1,
                    downCount = if (oldVotes.hasDownVote) oldVotes.downCount - 1 else oldVotes.downCount,
                    hasUpVote = true,
                    hasDownVote = false
                )
            }
            posts[posts.indexOf(foundedPost)] = updatedPost
        }
    }

    private fun updateDownVoteCountOfVotes(
        state: Paginator.State?,
        contentId: ContentId
    ): Paginator.State? {
        when (state) {
            is Paginator.State.Data<*> -> {
                updateDownVoteInPostsByContentId(((state).data as ArrayList<Post>), contentId)
            }
            is Paginator.State.Refresh<*> -> {
                updateDownVoteInPostsByContentId(((state).data as ArrayList<Post>), contentId)

            }
            is Paginator.State.NewPageProgress<*> -> {
                updateDownVoteInPostsByContentId(((state).data as ArrayList<Post>), contentId)

            }
            is Paginator.State.FullData<*> -> {
                updateDownVoteInPostsByContentId(((state).data as ArrayList<Post>), contentId)
            }
        }
        return state
    }

    private fun updateDownVoteInPostsByContentId(posts: ArrayList<Post>, contentId: ContentId) {
        val foundedPost = posts.find { post ->
            post.contentId == contentId
        }
        val updatedPost = foundedPost?.copy()
        updatedPost?.let { post ->
            if (!post.votes.hasDownVote) {
                val oldVotes = post.votes
                post.votes = post.votes.copy(
                    downCount = post.votes.downCount + 1,
                    upCount = if (oldVotes.hasUpVote) oldVotes.upCount - 1 else oldVotes.upCount,
                    hasUpVote = false,
                    hasDownVote = true
                )
                posts[posts.indexOf(foundedPost)] = updatedPost
            }
        }
    }

    override fun onCleared() {
        loadPostsJob?.cancel()
        super.onCleared()
    }
}