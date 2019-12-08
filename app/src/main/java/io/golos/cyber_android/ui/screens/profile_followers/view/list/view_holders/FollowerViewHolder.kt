package io.golos.cyber_android.ui.screens.profile_followers.view.list.view_holders

import android.annotation.SuppressLint
import android.view.ViewGroup
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.common.characters.SpecialChars
import io.golos.cyber_android.ui.common.formatters.size.PluralSizeFormatter
import io.golos.cyber_android.ui.common.glide.loadAvatar
import io.golos.cyber_android.ui.common.recycler_view.ViewHolderBase
import io.golos.cyber_android.ui.common.recycler_view.versioned.VersionedListItem
import io.golos.cyber_android.ui.screens.profile_followers.dto.FollowersListItem
import io.golos.cyber_android.ui.screens.profile_followers.view.list.FollowersListItemEventsProcessor
import kotlinx.android.synthetic.main.view_profile_followers_list_item.view.*

class FollowerViewHolder(
    parentView: ViewGroup
) : ViewHolderBase<FollowersListItemEventsProcessor, VersionedListItem>(
    parentView,
    R.layout.view_profile_followers_list_item
) {
    private val followersFormatter = PluralSizeFormatter(
        parentView.context.applicationContext,
        R.plurals.formatter_followers_formatted
    )
    private val postsFormatter = PluralSizeFormatter(
        parentView.context.applicationContext,
        R.plurals.formatter_posts_formatted
    )

    @SuppressLint("SetTextI18n")
    override fun init(listItem: VersionedListItem, listItemEventsProcessor: FollowersListItemEventsProcessor) {
        if(listItem !is FollowersListItem) {
            return
        }

        with(listItem) {
            itemView.title.text = follower.userName

            val followers = followersFormatter.format(follower.followersCount!!)
            val posts = postsFormatter.format(follower.postsCount!!)

            itemView.info.text = "$followers ${SpecialChars.bullet} $posts"

            itemView.setOnClickListener { /*listItemEventsProcessor.onItemClick(community)*/ }

            if(isJoined) {
                itemView.joinButton.text = itemView.context.resources.getString(R.string.joined_to_community)
            } else {
                itemView.joinButton.text = itemView.context.resources.getString(R.string.join_to_community)
            }

            itemView.joinButton.setOnClickListener { listItemEventsProcessor.onJoinClick(follower.userId, filter) }

            itemView.avatar.loadAvatar(follower.userAvatar)
        }
    }

    override fun release() {
        itemView.joinButton.setOnClickListener(null)
        itemView.setOnClickListener(null)
    }
}