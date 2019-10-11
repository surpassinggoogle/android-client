package io.golos.domain.repositories

import io.golos.domain.entities.AuthState
import io.golos.domain.requestmodel.AuthRequest

interface AuthStateRepository : Repository<AuthState, AuthRequest>