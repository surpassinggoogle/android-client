package io.golos.domain.api

import io.golos.commun4j.model.UserProfile
import io.golos.commun4j.services.model.AuthResult
import io.golos.commun4j.services.model.GetProfileResult
import io.golos.commun4j.services.model.ResolvedProfile
import io.golos.commun4j.sharedmodel.AuthSecret
import io.golos.commun4j.sharedmodel.CyberName

interface AuthApi {
    fun setActiveUserCreds(user: CyberName, activeKey: String)

    fun getUserAccount(user: CyberName): UserProfile

    fun getUserProfile(userName: String, user: CyberName? = null): GetProfileResult

    fun getAuthSecret(): AuthSecret

    fun authWithSecret(user: String, cyberName: CyberName, secret: String, signedSecret: String): AuthResult
}
