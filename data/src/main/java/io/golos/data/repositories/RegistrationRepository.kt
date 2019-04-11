package io.golos.data.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.golos.data.api.RegistrationApi
import io.golos.domain.DispatchersProvider
import io.golos.domain.Logger
import io.golos.domain.Repository
import io.golos.domain.entities.UserRegistrationStateEntity
import io.golos.domain.model.*
import io.golos.domain.rules.CyberToEntityMapper
import io.golos.domain.rules.UserRegistrationStateRelatedData
import kotlinx.coroutines.*
import java.util.*

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-04-11.
 */
class RegistrationRepository(
    private val registrationApi: RegistrationApi,
    private val dispatchersProvider: DispatchersProvider,
    private val logger: Logger,
    private val toRegistrationStateEntityMapper: CyberToEntityMapper<UserRegistrationStateRelatedData, UserRegistrationStateEntity>
) : Repository<UserRegistrationStateEntity, RegistrationStepRequest> {

    private val repositoryScope = CoroutineScope(dispatchersProvider.uiDispatcher + SupervisorJob())

    private val registrationRequestsLiveData =
        MutableLiveData<Map<Identifiable.Id, QueryResult<RegistrationStepRequest>>>()

    private val registerJobsMap = Collections.synchronizedMap(HashMap<Identifiable.Id, Job>())

    private val registrationStates =
        Collections.synchronizedMap(HashMap<Identifiable.Id, MutableLiveData<UserRegistrationStateEntity>>())

    private val allMyDataRequest = ResendSmsVerificationCode("9999999999999999999999")

    override val allDataRequest: RegistrationStepRequest
        get() = allMyDataRequest

    override fun getAsLiveData(params: RegistrationStepRequest): LiveData<UserRegistrationStateEntity> {
        return registrationStates.getOrPut(params.id) {
            MutableLiveData()
        }
    }

    override fun makeAction(params: RegistrationStepRequest) {
        repositoryScope.launch {
            registerJobsMap[params.id]?.cancel()
            registrationRequestsLiveData.value =
                registrationRequestsLiveData.value.orEmpty() + (params.id to QueryResult.Loading(params))

            try {
                (getAsLiveData(params) as MutableLiveData).value =
                    withContext(dispatchersProvider.workDispatcher) {
                        val actionResult =
                            @Suppress("IMPLICIT_CAST_TO_ANY")
                        when (params) {
                            is GetUserRegistrationStepRequest -> null
                            is SendSmsForVerificationRequest ->
                                registrationApi.firstUserRegistrationStep(params.phone, params.testCode)
                            is SendVerificationCodeRequest ->
                                registrationApi.verifyPhoneForUserRegistration(params.phone, params.code)

                            is SetUserNameRequest ->
                                registrationApi.setVerifiedUserName(params.userName, params.phone)

                            is SetUserKeysRequest ->
                                registrationApi.writeUserToBlockChain(
                                    params.userName,
                                    params.ownerKey,
                                    params.activeKey,
                                    params.postingKey,
                                    params.memoKey
                                )
                            is ResendSmsVerificationCode ->
                                registrationApi.resendSmsCode(params.phone)
                        }

                        val regState = registrationApi.getRegistrationState(params.phone)

                        toRegistrationStateEntityMapper(
                            UserRegistrationStateRelatedData(
                                actionResult,
                                regState
                            )
                        )
                    }
                registrationRequestsLiveData.value =
                    registrationRequestsLiveData.value.orEmpty() + (params.id to QueryResult.Success(params))

            } catch (e: Exception) {
                logger(e)
                registrationRequestsLiveData.value =
                    registrationRequestsLiveData.value.orEmpty() + (params.id to QueryResult.Error(e, params))
            }

        }.let { job ->
            registerJobsMap[params.id] = job
        }
    }

    private suspend fun getUserRegState(phone: String): UserRegistrationStateEntity {
        return toRegistrationStateEntityMapper(
            UserRegistrationStateRelatedData(
                null,
                registrationApi.getRegistrationState(phone)
            )
        )
    }

    override val updateStates: LiveData<Map<Identifiable.Id, QueryResult<RegistrationStepRequest>>>
        get() = registrationRequestsLiveData
}