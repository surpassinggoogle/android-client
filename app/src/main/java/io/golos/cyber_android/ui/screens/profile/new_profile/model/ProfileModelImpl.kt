package io.golos.cyber_android.ui.screens.profile.new_profile.model

import io.golos.cyber_android.ui.common.mvvm.model.ModelBaseImpl
import io.golos.domain.dto.UserProfileDomain
import io.golos.domain.repositories.CurrentUserRepository
import io.golos.domain.use_cases.user.UsersRepository
import javax.inject.Inject

class ProfileModelImpl
@Inject
constructor(
    private val currentUserRepository: CurrentUserRepository,
    private val  usersRepository: UsersRepository
) : ModelBaseImpl(), ProfileModel {

    override suspend fun loadProfileInfo(): UserProfileDomain =
        usersRepository.getUserProfile(currentUserRepository.user)
}