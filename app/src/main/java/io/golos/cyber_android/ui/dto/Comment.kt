package io.golos.cyber_android.ui.dto

import io.golos.domain.use_cases.post.post_dto.ContentBlock

data class Comment(
    val contentId: ContentId,
    val author: Author,
    var votes: Votes,
    val body: ContentBlock?,
    val childCommentsCount: Int,
    val community: Post.Community,
    val meta: Meta,
    val parent: ParentComment,
    val type: String,
    val isDeleted: Boolean,
    val isMyComment: Boolean,
    val commentLevel: Int = 0
)