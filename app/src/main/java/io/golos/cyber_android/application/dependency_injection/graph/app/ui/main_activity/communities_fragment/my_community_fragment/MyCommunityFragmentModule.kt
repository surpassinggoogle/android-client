package io.golos.cyber_android.application.dependency_injection.graph.app.ui.main_activity.communities_fragment.my_community_fragment

import dagger.Module
import dagger.Provides
import io.golos.cyber_android.ui.screens.main_activity.communities.data_repository.dto.CommunityType

@Module
class MyCommunityFragmentModule {
    @Provides
    internal fun provideCommunityType(): CommunityType = CommunityType.USER
}