package io.golos.cyber_android.ui.screens.my_feed.view_model

import io.golos.cyber_android.ui.common.widgets.post.*
import io.golos.cyber_android.ui.dto.ContentId
import io.golos.cyber_android.ui.dto.Post

interface MyFeedListListener :
    RichWidgetListener,
    EmbedWidgetListener,
    AttachmentWidgetListener,
    EmbedImageWidgetListener,
    EmbedWebsiteWidgetListener,
    EmbedVideoWidgetListener,
    ParagraphWidgetListener,
    PostCommentsListener,
    PostVotesListener,
    PostShareListener,
    MenuListener

interface PostCommentsListener {

    fun onCommentsClicked(postContentId: ContentId)
}

interface PostShareListener {

    fun onShareClicked(shareUrl: String)
}

interface PostVotesListener {

    fun onUpVoteClicked(post: ContentId)

    fun onDownVoteClicked(post: ContentId)
}