package io.golos.cyber_android.ui.screens.post_edit.model

import io.golos.commun4j.sharedmodel.Either
import io.golos.cyber_android.ui.shared.mvvm.model.ModelBase
import io.golos.cyber_android.ui.dto.ContentId
import io.golos.cyber_android.ui.screens.post_edit.dto.ExternalLinkError
import io.golos.cyber_android.ui.screens.post_edit.dto.ExternalLinkInfo
import io.golos.cyber_android.ui.screens.post_edit.dto.ValidationResult
import io.golos.domain.commun_entities.CommunityId
import io.golos.domain.commun_entities.Permlink
import io.golos.domain.dto.CommunityDomain
import io.golos.domain.dto.ContentIdDomain
import io.golos.domain.dto.PostDomain
import io.golos.domain.dto.UploadedImageEntity
import io.golos.domain.use_cases.model.PostModel
import io.golos.domain.use_cases.post.editor_output.ControlMetadata

interface EditorPageModel : ModelBase {
    suspend fun getExternalLinkInfo(uri: String): Either<ExternalLinkInfo, ExternalLinkError>

    fun validatePost(title: String, content: List<ControlMetadata>): ValidationResult

    /**
     * @return null if no image to upload otherwise - operation contentId
     */
    suspend fun uploadLocalImage(content: List<ControlMetadata>): UploadedImageEntity?

    suspend fun createPost(
        content: List<ControlMetadata>,
        adultOnly: Boolean,
        communityId: CommunityId,
        localImagesUri: List<String> = emptyList()
    ): ContentIdDomain

    suspend fun updatePost(
        contentIdDomain: ContentIdDomain,
        content: List<ControlMetadata>,
        permlink: Permlink,
        adultOnly: Boolean,
        localImagesUri: List<String> = emptyList()
    ): ContentIdDomain

    suspend fun getLastUsedCommunity(): CommunityDomain?

    suspend fun saveLastUsedCommunity(community: CommunityDomain)

    suspend fun getPostToEdit(contentId: ContentId): PostDomain

    @Deprecated("Use getPostToEdit with contentId param")
    suspend fun getPostToEdit(permlink: Permlink): PostModel
}