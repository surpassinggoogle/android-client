package io.golos.cyber_android.ui.screens.profile_followers.dto

import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem
import io.golos.cyber_android.ui.dto.FollowersFilter

/**
 * List item for loading indicator
 */
data class RetryListItem(
    override val id: Long,
    override val version: Long,
    val filter: FollowersFilter
): VersionedListItem