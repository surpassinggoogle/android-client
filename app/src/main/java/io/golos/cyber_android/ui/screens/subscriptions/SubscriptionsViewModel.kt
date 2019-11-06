package io.golos.cyber_android.ui.screens.subscriptions

import androidx.lifecycle.MutableLiveData
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.common.mvvm.viewModel.ViewModelBase
import io.golos.cyber_android.ui.common.mvvm.view_commands.BackCommand
import io.golos.cyber_android.ui.common.mvvm.view_commands.NavigateToSearchCommunitiesCommand
import io.golos.cyber_android.ui.common.mvvm.view_commands.SetLoadingVisibilityCommand
import io.golos.cyber_android.ui.common.mvvm.view_commands.ShowMessageCommand
import io.golos.cyber_android.ui.common.paginator.Paginator
import io.golos.cyber_android.ui.screens.subscriptions.mappers.CommunityDomainListToCommunityListMapper
import io.golos.cyber_android.utils.EMPTY
import io.golos.cyber_android.utils.toLiveData
import io.golos.domain.DispatchersProvider
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class SubscriptionsViewModel @Inject constructor(
    dispatchersProvider: DispatchersProvider,
    model: SubscriptionsModel,
    private val paginatorSubscriptions: Paginator.Store<Community>,
    private val paginatorRecommendedCommunities: Paginator.Store<Community>
) :

    ViewModelBase<SubscriptionsModel>(dispatchersProvider, model) {

    private val _generalLoadingProgressVisibilityLiveData: MutableLiveData<Boolean> = MutableLiveData(false)

    private val _generalErrorVisibilityLiveData: MutableLiveData<Boolean> = MutableLiveData(false)

    private val _subscriptionsState: MutableLiveData<SubscriptionsState> = MutableLiveData(SubscriptionsState.UNDEFINED)

    private val _subscriptionsListStateLiveData: MutableLiveData<Paginator.State> = MutableLiveData(Paginator.State.Empty)

    private val _recommendedSubscriptionsListStateLiveData: MutableLiveData<Paginator.State> =
        MutableLiveData(Paginator.State.Empty)

    private val _recommendedSubscriptionStatusLiveData: MutableLiveData<Community> = MutableLiveData()

    private val _subscriptionStatusLiveData: MutableLiveData<Community> = MutableLiveData()

    private val _searchProgressVisibilityLiveData: MutableLiveData<Boolean> = MutableLiveData(false)

    val subscriptionsListStateLiveData = _subscriptionsListStateLiveData.toLiveData()

    val recommendedSubscriptionsListStateLiveData = _recommendedSubscriptionsListStateLiveData.toLiveData()

    val subscriptionsStateLiveData = _subscriptionsState.toLiveData()

    val generalLoadingProgressVisibilityLiveData = _generalLoadingProgressVisibilityLiveData.toLiveData()

    val recommendedSubscriptionStatusLiveData = _recommendedSubscriptionStatusLiveData.toLiveData()

    val subscriptionsStatusLiveData = _subscriptionStatusLiveData.toLiveData()

    val generalErrorVisibilityLiveData = _generalErrorVisibilityLiveData.toLiveData()

    val searchProgressVisibilityLiveData = _searchProgressVisibilityLiveData.toLiveData()

    private var communitySearchQuery: String = EMPTY

    private var getCommunitiesJob: Job? = null

    init {
        paginatorSubscriptions.sideEffectListener = {
            when (it) {
                is Paginator.SideEffect.LoadPage -> loadCommunities(it.pageCount)
                is Paginator.SideEffect.ErrorEvent -> {
                    _searchProgressVisibilityLiveData.value = false
                }
            }
        }
        paginatorSubscriptions.render = {
            _subscriptionsListStateLiveData.value = it
            _searchProgressVisibilityLiveData.value = it is Paginator.State.SearchProgress<*>
        }

        paginatorRecommendedCommunities.sideEffectListener = {
            when (it) {
                is Paginator.SideEffect.LoadPage -> loadRecommendedCommunities(it.pageCount)
            }
        }
        paginatorRecommendedCommunities.render = {
            _recommendedSubscriptionsListStateLiveData.value = it
        }
    }

    private fun loadCommunities(pageCount: Int) {
        getCommunitiesJob?.cancel()
        getCommunitiesJob = launch {
            try {
                val communitiesByQueryPage = model.getCommunitiesByQuery(communitySearchQuery, pageCount * PAGE_SIZE_LIMIT, PAGE_SIZE_LIMIT)
                paginatorSubscriptions.proceed(
                    Paginator.Action.NewPage(
                        pageCount,
                        CommunityDomainListToCommunityListMapper().invoke(communitiesByQueryPage)
                    )
                )
            } catch (e: java.lang.Exception) {
                Timber.e(e)
                paginatorSubscriptions.proceed(Paginator.Action.PageError(e))
            }
        }
    }

    private fun loadRecommendedCommunities(pageCount: Int) {
        launch {
            try {
                val recommendedCommunitiesPage = model.getRecommendedCommunities(pageCount * PAGE_SIZE_LIMIT, PAGE_SIZE_LIMIT)
                paginatorRecommendedCommunities.proceed(
                    Paginator.Action.NewPage(
                        pageCount,
                        CommunityDomainListToCommunityListMapper().invoke(recommendedCommunitiesPage)
                    )
                )
            } catch (e: java.lang.Exception) {
                Timber.e(e)
                paginatorRecommendedCommunities.proceed(Paginator.Action.PageError(e))
            }
        }
    }

    fun onFindCommunitiesClicked() {
        command.value = NavigateToSearchCommunitiesCommand()
    }

    fun start() {
        val subscriptionsState = _subscriptionsState.value
        if (subscriptionsState == SubscriptionsState.UNDEFINED || subscriptionsState == SubscriptionsState.ERROR) {
            launch {
                try {
                    _generalErrorVisibilityLiveData.value = false
                    _generalLoadingProgressVisibilityLiveData.value = true
                    val recommendedCommunitiesPage = model.getRecommendedCommunities(0, PAGE_SIZE_LIMIT)
                    val communities = model.getCommunitiesByQuery(communitySearchQuery, 0, PAGE_SIZE_LIMIT)
                    if (communities.isEmpty()) {
                        val recommendedCommunities =
                            CommunityDomainListToCommunityListMapper().invoke(recommendedCommunitiesPage)
                        val state = Paginator.State.Data(0, recommendedCommunities)
                        paginatorRecommendedCommunities.initState(state)
                        _subscriptionsState.value = SubscriptionsState.EMPTY
                        _recommendedSubscriptionsListStateLiveData.value = state
                    } else {
                        val recommendedCommunities =
                            CommunityDomainListToCommunityListMapper().invoke(communities)
                        val state = Paginator.State.Data(0, recommendedCommunities)
                        paginatorSubscriptions.initState(state)
                        _subscriptionsState.value = SubscriptionsState.EXIST
                        _subscriptionsListStateLiveData.value = state
                    }
                    _generalLoadingProgressVisibilityLiveData.value = false
                } catch (e: Exception) {
                    Timber.e(e)
                    _subscriptionsState.value = SubscriptionsState.ERROR
                    _generalLoadingProgressVisibilityLiveData.value = false
                    _generalErrorVisibilityLiveData.value = true
                }
            }
        }
    }

    fun onCommunitySearchQueryChanged(query: String) {
        if(communitySearchQuery == query){
            _searchProgressVisibilityLiveData.value = false
        } else{
            communitySearchQuery = query
            getCommunitiesJob?.cancel()
            paginatorSubscriptions.proceed(Paginator.Action.Search)
        }
    }

    fun loadMoreRecommendedCommunities() {
        paginatorRecommendedCommunities.proceed(Paginator.Action.LoadMore)
    }

    fun loadSubscriptions() {
        paginatorSubscriptions.proceed(Paginator.Action.LoadMore)
    }

    fun changeCommunitySubscriptionStatus(community: Community) {
        launch {
            try {
                command.value = SetLoadingVisibilityCommand(true)
                val communityId = community.communityId
                if (community.isSubscribed) {
                    model.unsubscribeToCommunity(communityId)
                } else {
                    model.subscribeToCommunity(communityId)
                }
                val isRecommendedState = _subscriptionsState.value == SubscriptionsState.EMPTY
                val state: Paginator.State = if (isRecommendedState) {
                    _recommendedSubscriptionsListStateLiveData.value!!
                } else {
                    _subscriptionsListStateLiveData.value!!
                }
                community.isSubscribed = !community.isSubscribed
                val updatedState = updateCommunitySubscriptionStatusInState(state, community)
                if (isRecommendedState) {
                    _recommendedSubscriptionsListStateLiveData.value = updatedState
                    _recommendedSubscriptionStatusLiveData.value = community
                } else {
                    _subscriptionsListStateLiveData.value = updatedState
                    _subscriptionStatusLiveData.value = community
                }
                command.value = SetLoadingVisibilityCommand(false)
            } catch (e: Exception) {
                Timber.e(e)
                command.value = ShowMessageCommand(R.string.loading_error)
                command.value = SetLoadingVisibilityCommand(false)
            }
        }

    }

    private fun updateCommunitySubscriptionStatusInState(state: Paginator.State, community: Community): Paginator.State {
        when (state) {
            is Paginator.State.Data<*> -> {
                val findCommunity = (state.data as List<Community>).find { it == community }
                findCommunity?.isSubscribed = community.isSubscribed
            }
            is Paginator.State.Refresh<*> -> {
                val findCommunity = (state.data as List<Community>).find { it == community }
                findCommunity?.isSubscribed = community.isSubscribed
            }
            is Paginator.State.NewPageProgress<*> -> {
                val findCommunity = (state.data as List<Community>).find { it == community }
                findCommunity?.isSubscribed = community.isSubscribed
            }
            is Paginator.State.FullData<*> -> {
                val findCommunity = (state.data as List<Community>).find { it == community }
                findCommunity?.isSubscribed = community.isSubscribed
            }
        }
        return state
    }

    fun back() {
        command.value = BackCommand()
    }

    enum class SubscriptionsState {
        /**
         * Yet not unknown count subscriptions
         */
        UNDEFINED,

        /**
         * Count user subscriptions on community = 0
         */
        EMPTY,

        /**
         * Exist subscriptions in community
         */
        EXIST,

        /**
         * Error get data
         */
        ERROR
    }

    private companion object {

        private const val PAGE_SIZE_LIMIT = 30
    }
}