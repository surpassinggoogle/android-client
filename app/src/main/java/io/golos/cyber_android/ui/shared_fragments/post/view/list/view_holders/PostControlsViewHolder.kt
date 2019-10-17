package io.golos.cyber_android.ui.shared_fragments.post.view.list.view_holders

import android.view.ViewGroup
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.common.recycler_view.ViewHolderBase
import io.golos.cyber_android.ui.shared_fragments.post.dto.post_list_items.PostControlsListItem
import io.golos.cyber_android.ui.shared_fragments.post.view_model.PostPageViewModelItemsClickProcessor
import kotlinx.android.synthetic.main.item_post_controls.view.*

class PostControlsViewHolder(
    parentView: ViewGroup
) : ViewHolderBase<PostPageViewModelItemsClickProcessor, PostControlsListItem>(
    parentView,
    R.layout.item_post_controls
) {
    override fun init(listItem: PostControlsListItem, listItemEventsProcessor: PostPageViewModelItemsClickProcessor) {
        itemView.votesText.text = listItem.voteBalance.toString()

        val countersFormatter = io.golos.cyber_android.ui.common.formatters.counts.KiloCounterFormatter()

        itemView.viewCountsText.text = countersFormatter.format(listItem.totalViews)
        itemView.commentsCountText.text = countersFormatter.format(listItem.totalComments)

        itemView.upvoteButton.setOnClickListener { listItemEventsProcessor.onUpVoteClick() }
        itemView.downvoteButton.setOnClickListener { listItemEventsProcessor.onDownVoteClick() }
    }

    override fun release() {
        itemView.upvoteButton.setOnClickListener(null)
        itemView.downvoteButton.setOnClickListener(null)
    }
}