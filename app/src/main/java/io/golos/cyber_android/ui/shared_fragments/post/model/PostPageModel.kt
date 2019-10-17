package io.golos.cyber_android.ui.shared_fragments.post.model

import androidx.lifecycle.LiveData
import io.golos.cyber_android.ui.common.mvvm.model.ModelBase
import io.golos.cyber_android.ui.common.recycler_view.versioned.VersionedListItem
import io.golos.cyber_android.ui.shared_fragments.post.dto.PostHeader
import io.golos.domain.interactors.model.DiscussionIdModel
import io.golos.domain.interactors.model.PostModel
import io.golos.domain.post.post_dto.PostMetadata

interface PostPageModel : ModelBase {
    val postId: DiscussionIdModel

    val postMetadata: PostMetadata

    val post: LiveData<List<VersionedListItem>>

    suspend fun loadPost()

    fun getPostHeader(): PostHeader

    suspend fun getUserId(userName: String): String

    suspend fun deletePost()
}