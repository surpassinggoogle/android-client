package io.golos.cyber_android.application.dependency_injection.graph.app.ui.main_activity.communities_fragment.discover_fragment

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.golos.cyber_android.ui.common.formatters.size.FollowersSizeFormatter
import io.golos.cyber_android.ui.common.formatters.size.SizeFormatter
import io.golos.cyber_android.ui.common.mvvm.viewModel.FragmentViewModelFactory
import io.golos.cyber_android.ui.common.mvvm.viewModel.FragmentViewModelFactoryImpl
import io.golos.cyber_android.ui.common.mvvm.viewModel.ViewModelKey
import io.golos.cyber_android.ui.screens.main_activity.communities.tabs.common.viewModel.CommunityViewModel
import io.golos.cyber_android.ui.screens.main_activity.communities.tabs.common.model.CommunityModel
import io.golos.cyber_android.ui.screens.main_activity.communities.tabs.common.model.CommunityModelImpl
import io.golos.cyber_android.ui.screens.main_activity.communities.tabs.common.model.search.CommunitiesSearch
import io.golos.cyber_android.ui.screens.main_activity.communities.tabs.common.model.search.CommunitiesSearchImpl
import io.golos.domain.dependency_injection.scopes.SubFragmentScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi

@ObsoleteCoroutinesApi
@ExperimentalCoroutinesApi
@Module
abstract class DiscoverFragmentModuleBinds {
    @Binds
    @SubFragmentScope
    abstract fun provideViewModelFactory(factory: FragmentViewModelFactoryImpl): FragmentViewModelFactory

    @Binds
    @IntoMap
    @ViewModelKey(CommunityViewModel::class)
    abstract fun provideDiscoverViewModel(viewModel: CommunityViewModel): ViewModel

    @Binds
    abstract fun provideDiscoverModel(model: CommunityModelImpl): CommunityModel

    @Binds
    abstract fun provideSearchEngine(engine: CommunitiesSearchImpl): CommunitiesSearch
}