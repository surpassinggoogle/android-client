package io.golos.domain.requestmodel

import io.golos.commun4j.model.Beneficiary
import io.golos.commun4j.model.DiscussionCreateMetadata
import io.golos.commun4j.model.Tag
import io.golos.commun4j.sharedmodel.CyberName
import io.golos.domain.commun_entities.Permlink


/**
 * Created by yuri yurivladdurain@gmail.com on 2019-04-02.
 */
sealed class DiscussionCreateRequest

data class CreateCommentRequest(
    val body: String,
    val parentAccount: CyberName,
    val parentPermlink: Permlink,
    val category: List<Tag>,
    val metadata: DiscussionCreateMetadata,
    val beneficiaries: List<Beneficiary>,
    val vestPayment: Boolean,
    val tokenProp: Long
) : DiscussionCreateRequest()

data class CreatePostRequest(
    val title: String,
    val body: String,
    val tags: List<Tag>,
    val communityId: String,
    val metadata: DiscussionCreateMetadata,
    val beneficiaries: List<Beneficiary>,
    val vestPayment: Boolean,
    val tokenProp: Long
):DiscussionCreateRequest()

data class UpdatePostRequest(
    val postPermlink: Permlink,
    val title: String,
    val body: String,
    val tags: List<Tag>,
    val metadata: DiscussionCreateMetadata
):DiscussionCreateRequest()

data class DeleteDiscussionRequest(
    val permlink: Permlink
):DiscussionCreateRequest()