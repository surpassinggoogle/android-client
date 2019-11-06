package io.golos.cyber_android.ui.screens.feed

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.golos.cyber_android.ui.common.mvvm.model.ModelBase
import io.golos.cyber_android.ui.common.mvvm.viewModel.ViewModelBase
import io.golos.domain.DispatchersProvider
import javax.inject.Inject

class FeedViewModel @Inject constructor(
    dispatchersProvider: DispatchersProvider
): ViewModelBase<ModelBase>(dispatchersProvider) {

    sealed class Event {
        data class SearchEvent(val query: String): Event()
        object RefreshRequestEvent: Event()
    }

    private val eventsLiveData = MutableLiveData<Event>()

    val getEventsLiveData = eventsLiveData as LiveData<Event>

    fun onSearch(query: String) {
        eventsLiveData.postValue(Event.SearchEvent(query))
    }
}