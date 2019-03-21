package io.golos.cyber_android.ui.screens.feed

import androidx.lifecycle.MutableLiveData
import io.golos.cyber_android.ui.common.posts.AbstractFeedViewModel
import io.golos.cyber_android.widgets.EditorWidget
import io.golos.cyber_android.widgets.sorting.TimeFilter
import io.golos.cyber_android.widgets.sorting.TrendingSort
import io.golos.domain.interactors.feed.AbstractFeedUseCase
import io.golos.domain.interactors.feed.CommunityFeedUseCase
import io.golos.domain.model.CommunityFeedUpdateRequest
import io.golos.domain.model.PostFeedUpdateRequest

open class FeedPageTabViewModel<out T : PostFeedUpdateRequest>(feedUseCase: AbstractFeedUseCase<out T>) :
    AbstractFeedViewModel<T>(feedUseCase) {

    //val timeFilterLiveDate = MutableLiveData<TimeFilter>()
    val editorWidgetStateLiveData = MutableLiveData<EditorWidget.EditorWidgetState>(
        EditorWidget.EditorWidgetState("https://www.w3schools.com/howto/img_avatar.png")
    )

    fun onSearch(query: String) {

    }

    fun onSort(sort: TrendingSort) {

    }

    fun onFilter(filter: TimeFilter) {

    }
}