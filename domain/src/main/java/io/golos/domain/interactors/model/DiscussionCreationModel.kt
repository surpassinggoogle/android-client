package io.golos.domain.interactors.model

import io.golos.domain.Model

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-04-02.
 */
sealed class DiscussionCreationRequest : Model

data class PostCreationRequestModel(
    val title: String,
    val body: CharSequence,//in real app use only spanned
    val tags: List<String>
) : DiscussionCreationRequest()

data class CommentCreationRequestModel(
    val body: CharSequence,//in real app use only spanned
    val parentId: DiscussionIdModel,
    val tags: List<String>
) : DiscussionCreationRequest()
