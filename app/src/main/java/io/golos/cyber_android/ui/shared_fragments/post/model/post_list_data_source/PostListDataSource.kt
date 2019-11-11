package io.golos.cyber_android.ui.shared_fragments.post.model.post_list_data_source

import androidx.lifecycle.LiveData
import io.golos.cyber_android.ui.common.recycler_view.versioned.VersionedListItem
import io.golos.cyber_android.ui.shared_fragments.post.dto.SortingType
import io.golos.domain.use_cases.model.PostModel

interface PostListDataSource {
    val post: LiveData<List<VersionedListItem>>

    suspend fun createOrUpdatePostData(postModel: PostModel)

    suspend fun updateCommentsSorting(sortingType: SortingType)
}