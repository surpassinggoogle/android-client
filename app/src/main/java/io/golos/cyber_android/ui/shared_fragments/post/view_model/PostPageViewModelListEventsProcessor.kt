package io.golos.cyber_android.ui.shared_fragments.post.view_model

import io.golos.cyber_android.ui.shared_fragments.post.view.list.view_holders.post_body.widgets.*
import io.golos.domain.use_cases.model.DiscussionIdModel

/**
 * Processing post items clicks
 */
interface PostPageViewModelListEventsProcessor :
    AttachmentWidgetListener,
    EmbedImageWidgetListener,
    EmbedWebsiteWidgetListener,
    EmbedVideoWidgetListener,
    ParagraphWidgetListener,
    PostCommentVoteListener,
    PostVoteListener,
    CommentsListener

interface PostCommentVoteListener : BasePostBlockWidgetListener{

    fun onCommentUpVoteClick(commentId: DiscussionIdModel)

    fun onCommentDownVoteClick(commentId: DiscussionIdModel)
}

interface PostVoteListener : BasePostBlockWidgetListener{

    fun onUpVoteClick()

    fun onDownVoteClick()
}

interface CommentsListener : BasePostBlockWidgetListener{

    fun onCommentsTitleMenuClick()

    fun onNextCommentsPageReached()

    fun onRetryLoadingFirstLevelCommentButtonClick()

    fun onCollapsedCommentsClick(parentCommentId: DiscussionIdModel)

    fun onRetryLoadingSecondLevelCommentButtonClick(parentCommentId: DiscussionIdModel)

    fun onCommentLongClick(commentId: DiscussionIdModel)

    fun startReplyToComment(commentToReplyId: DiscussionIdModel)
}