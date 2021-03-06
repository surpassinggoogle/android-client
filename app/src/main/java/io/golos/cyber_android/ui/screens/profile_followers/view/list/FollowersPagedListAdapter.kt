package io.golos.cyber_android.ui.screens.profile_followers.view.list

import android.view.ViewGroup
import io.golos.cyber_android.ui.dto.FollowersFilter
import io.golos.cyber_android.ui.screens.profile_followers.dto.FollowersListItem
import io.golos.cyber_android.ui.screens.profile_followers.dto.LoadingListItem
import io.golos.cyber_android.ui.screens.profile_followers.dto.NoDataListItem
import io.golos.cyber_android.ui.screens.profile_followers.dto.RetryListItem
import io.golos.cyber_android.ui.screens.profile_followers.view.list.view_holders.FollowerViewHolder
import io.golos.cyber_android.ui.screens.profile_followers.view.list.view_holders.LoadingViewHolder
import io.golos.cyber_android.ui.screens.profile_followers.view.list.view_holders.NoDataViewHolder
import io.golos.cyber_android.ui.screens.profile_followers.view.list.view_holders.RetryViewHolder
import io.golos.cyber_android.ui.shared.recycler_view.ViewHolderBase
import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListAdapterBase
import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem

open class FollowersPagedListAdapter(
    private val listItemEventsProcessor: FollowersListItemEventsProcessor,
    pageSize: Int?,
    private val filter: FollowersFilter
) : VersionedListAdapterBase<FollowersListItemEventsProcessor>(listItemEventsProcessor, pageSize) {

    protected companion object {
        const val FOLLOWER = 0
        const val LOADING = 1
        const val RETRY = 2
        const val NO_DATA = 3
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderBase<FollowersListItemEventsProcessor, VersionedListItem> =
        when(viewType) {
            FOLLOWER -> FollowerViewHolder(parent)
            LOADING -> LoadingViewHolder(parent)
            RETRY -> RetryViewHolder(parent)
            NO_DATA -> NoDataViewHolder(parent)
            else -> throw UnsupportedOperationException("This type of item is not supported")
        }

    override fun getItemViewType(position: Int): Int =
        when(items[position]) {
            is FollowersListItem -> FOLLOWER
            is LoadingListItem -> LOADING
            is RetryListItem -> RETRY
            is NoDataListItem -> NO_DATA
            else -> throw UnsupportedOperationException("This type of item is not supported")
        }

    override fun onNextPageReached() {  listItemEventsProcessor.onNextPageReached(filter) }
}