package io.golos.cyber_android.ui.screens.profile.edit

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import io.golos.domain.interactors.user.UserMetadataUseCase

/**
 * Base view model that allows to listen updates in user metadata (via [getMetadataLiveData]) and
 * update state of it (via [getMetadataUpdateStateLiveData]).
 */
abstract class BaseEditProfileViewModel(private val userMetadataUseCase: UserMetadataUseCase) : ViewModel() {

    /**
     * [LiveData] for user metadata
     */
    val getMetadataLiveData = userMetadataUseCase.getAsLiveData

    /**
     * [LiveData] for updating state in user metadata, with this you can listen to update state after
     * [UserMetadataUseCase.updateMetadata] was called.
     */
    val getMetadataUpdateStateLiveData = userMetadataUseCase.getUpdateResultLiveData

    init {
        userMetadataUseCase.subscribe()
    }

    override fun onCleared() {
        super.onCleared()
        userMetadataUseCase.unsubscribe()
    }
}
