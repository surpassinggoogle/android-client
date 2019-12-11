package io.golos.cyber_android.ui.screens.main_activity.view_model

import io.golos.cyber_android.R
import io.golos.cyber_android.ui.common.mvvm.viewModel.ViewModelBase
import io.golos.cyber_android.ui.common.mvvm.view_commands.NavigationCommand
import io.golos.cyber_android.ui.screens.main_activity.model.MainModel
import io.golos.domain.DispatchersProvider
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class MainViewModel @Inject constructor(dispatchersProvider: DispatchersProvider, mainModel: MainModel) :
    ViewModelBase<MainModel>(dispatchersProvider, mainModel) {

    init {
        launch {
            try {
                if (model.isNeedShowFtueBoard()) {
                    _command.value = NavigationCommand(R.id.ftueFragment)
                } else {
                    _command.value = NavigationCommand(R.id.dashboardFragment)
                }
            } catch (e: Exception) {
                Timber.e(e)
            }
        }
    }
}