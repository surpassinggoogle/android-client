package io.golos.cyber_android.ui.screens.post_edit.di

import androidx.lifecycle.ViewModel
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import io.golos.cyber_android.ui.shared.mvvm.viewModel.ViewModelKey
import io.golos.cyber_android.ui.dto.ContentId
import io.golos.cyber_android.ui.screens.post_edit.model.EditorPageModel
import io.golos.cyber_android.ui.screens.post_edit.view_model.EditorPageViewModel
import io.golos.domain.DispatchersProvider
import io.golos.domain.commun_entities.Permlink
import io.golos.domain.dto.CommentEntity
import io.golos.domain.dto.PostEntity
import io.golos.domain.dto.VoteRequestEntity
import io.golos.domain.mappers.CommentsFeedEntityToModelMapper
import io.golos.domain.mappers.PostEntitiesToModelMapper
import io.golos.domain.repositories.DiscussionsFeedRepository
import io.golos.domain.repositories.Repository
import io.golos.domain.requestmodel.CommentFeedUpdateRequest
import io.golos.domain.requestmodel.PostFeedUpdateRequest
import io.golos.domain.use_cases.UseCase
import io.golos.domain.use_cases.feed.PostWithCommentUseCaseImpl
import io.golos.domain.use_cases.model.DiscussionIdModel
import io.golos.domain.use_cases.model.UploadedImagesModel
import io.golos.domain.use_cases.publish.DiscussionPosterUseCase
import io.golos.domain.use_cases.publish.EmbedsUseCase

@Module
class EditorPageFragmentModule(private val contentId: ContentId?) {

    @Provides
    internal fun provideContentId(): ContentId? = contentId

    @Provides
    internal fun provideDiscussionModelId(contentId: ContentId?): DiscussionIdModel? = contentId?.let { DiscussionIdModel(contentId.userId, Permlink(contentId.permlink)) }

    @Provides
    @IntoMap
    @ViewModelKey(EditorPageViewModel::class)
    internal fun provideEditorPageViewModel(
        embedsUseCase: EmbedsUseCase,
        posterUseCase: DiscussionPosterUseCase,
        imageUploadUseCase: UseCase<UploadedImagesModel>,
        postToEdit: DiscussionIdModel?,
        postFeedRepository: DiscussionsFeedRepository<PostEntity, PostFeedUpdateRequest>,
        postEntityToModelMapper: PostEntitiesToModelMapper,
        commentsRepository: DiscussionsFeedRepository<CommentEntity, CommentFeedUpdateRequest>,
        voteRepository: Repository<VoteRequestEntity, VoteRequestEntity>,
        commentFeeEntityToModelMapper: CommentsFeedEntityToModelMapper,
        dispatchersProvider: DispatchersProvider,
        model: EditorPageModel
    ): ViewModel {
        val postUseCase = if (postToEdit != null) {
            getPostWithCommentsUseCase(
                postToEdit,
                postFeedRepository,
                postEntityToModelMapper,
                commentsRepository,
                voteRepository,
                commentFeeEntityToModelMapper,
                dispatchersProvider
            )
        } else {
            null
        }

        return EditorPageViewModel(
            dispatchersProvider,
            embedsUseCase,
            posterUseCase,
            imageUploadUseCase,
            postUseCase,
            contentId,
            model
        )
    }

    private fun getPostWithCommentsUseCase(
        postId: DiscussionIdModel?,
        postFeedRepository: DiscussionsFeedRepository<PostEntity, PostFeedUpdateRequest>,
        postEntityToModelMapper: PostEntitiesToModelMapper,
        commentsRepository: DiscussionsFeedRepository<CommentEntity, CommentFeedUpdateRequest>,
        voteRepository: Repository<VoteRequestEntity, VoteRequestEntity>,
        commentFeeEntityToModelMapper: CommentsFeedEntityToModelMapper,
        dispatchersProvider: DispatchersProvider
    ): PostWithCommentUseCaseImpl =
        PostWithCommentUseCaseImpl(
            postId!!,
            postFeedRepository,
            postEntityToModelMapper,
            commentsRepository,
            voteRepository,
            commentFeeEntityToModelMapper,
            dispatchersProvider
        )
}