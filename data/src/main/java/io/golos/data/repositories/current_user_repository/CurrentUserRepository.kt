package io.golos.data.repositories.current_user_repository

import io.golos.domain.entities.AuthState

interface CurrentUserRepositoryRead {
    val authState: AuthState?
}

interface CurrentUserRepository : CurrentUserRepositoryRead {
    override var authState: AuthState?
}