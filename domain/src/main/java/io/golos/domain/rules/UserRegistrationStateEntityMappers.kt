package io.golos.domain.rules

import io.golos.cyber4j.model.FirstRegistrationStepResult
import io.golos.cyber4j.model.UserRegistrationState
import io.golos.cyber4j.model.UserRegistrationStateResult
import io.golos.domain.entities.*
import java.util.*

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-04-11.
 */
class UserRegistrationStateRelatedData(
    val requestResult: Any?,
    val stateRequestResult: UserRegistrationStateResult
)

class UserRegistrationStateEntityMapper :
    CyberToEntityMapper<UserRegistrationStateRelatedData, UserRegistrationStateEntity> {
    override suspend fun invoke(cyberObject: UserRegistrationStateRelatedData): UserRegistrationStateEntity {
        val stateRequestResult = cyberObject.stateRequestResult
        val requestResult = cyberObject.requestResult

        return when (cyberObject.stateRequestResult.state
            ?: throw IllegalArgumentException("server didn't returned reg state of user")) {

            UserRegistrationState.REGISTERED -> RegisteredUser(
                stateRequestResult.user ?: throw IllegalStateException(
                    "server" +
                            "didn't returned user name for some reason"
                )
            )
            UserRegistrationState.TO_BLOCK_CHAIN -> UnWrittenToBlockChainUser(
                stateRequestResult.user ?: throw IllegalStateException(
                    "server" +
                            "didn't returned user name for some reason"
                )
            )
            UserRegistrationState.VERIFY -> {
                val firstStepResult = requestResult as? FirstRegistrationStepResult
                return if (firstStepResult == null) UnverifiedUser(Date(Long.MIN_VALUE))
                else {
                    UnverifiedUser(requestResult.nextSmsRetry, requestResult.code)
                }
            }
            UserRegistrationState.SET_USER_NAME -> VerifiedUserWithoutUserName()
            UserRegistrationState.FIRST_STEP -> UnregisteredUser()
        }
    }
}