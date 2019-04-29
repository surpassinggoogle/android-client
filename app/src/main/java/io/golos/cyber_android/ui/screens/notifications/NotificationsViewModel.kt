package io.golos.cyber_android.ui.screens.notifications

import androidx.arch.core.util.Function
import androidx.lifecycle.ViewModel
import io.golos.domain.interactors.model.UpdateOption
import io.golos.domain.interactors.notifs.events.EventsUseCase
import io.golos.domain.map
import io.golos.domain.model.EventsListModel
import io.golos.domain.model.QueryResult


class NotificationsViewModel(private val eventsUseCase: EventsUseCase): ViewModel() {
    companion object {
        const val PAGE_SIZE = 20
    }

    /**
     * [LiveData] that indicates if data is loading
     */
    val loadingStatusLiveData = eventsUseCase.getUpdatingState.map(Function<QueryResult<UpdateOption>, Boolean> {
        it is QueryResult.Loading
    })

    val readinessLiveData = eventsUseCase.getReadinessLiveData

    val errorStatusLiveData = eventsUseCase.getUpdatingState.map(Function<QueryResult<UpdateOption>, Boolean> {
        it is QueryResult.Error
    })

    /**
     * [LiveData] that indicates if last page was reached
     */
    val lastPageLiveData = eventsUseCase.getAsLiveData.map(Function<EventsListModel, Boolean> {
        it.size % PAGE_SIZE != 0
    })


    /**
     * [LiveData] of all the [EventModel] items
     */
    val feedLiveData = eventsUseCase.getAsLiveData

    init {
        eventsUseCase.subscribe()
        requestRefresh()
    }

    fun requestRefresh() {
        eventsUseCase.requestUpdate(PAGE_SIZE, UpdateOption.REFRESH_FROM_BEGINNING)
    }

    fun loadMore() {
        eventsUseCase.requestUpdate(PAGE_SIZE, UpdateOption.FETCH_NEXT_PAGE)
    }

    override fun onCleared() {
        super.onCleared()
        eventsUseCase.unsubscribe()
    }
}