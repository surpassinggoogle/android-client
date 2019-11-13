package io.golos.cyber_android.ui.screens.main_activity.communities.view.list.view_holders

import android.view.ViewGroup
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.common.recycler_view.ViewHolderBase
import io.golos.cyber_android.ui.common.recycler_view.versioned.VersionedListItem
import io.golos.cyber_android.ui.screens.main_activity.communities.view.list.CommunityListItemEventsProcessor
import kotlinx.android.synthetic.main.view_communities_retry_list_item.view.*

class RetryListItemViewHolder(
    parentView: ViewGroup
) : ViewHolderBase<CommunityListItemEventsProcessor, VersionedListItem>(
    parentView,
    R.layout.view_communities_retry_list_item
) {
    override fun init(listItem: VersionedListItem, listItemEventsProcessor: CommunityListItemEventsProcessor) {
        itemView.pageLoadingRetryButton.setOnClickListener { listItemEventsProcessor.retry() }

    }

    override fun release() {
        itemView.pageLoadingRetryButton.setOnClickListener(null)
    }
}