package io.golos.use_cases.sign_up.transitions

import io.golos.domain.dto.sign_up.SignUpSnapshotDomain
import io.golos.domain.dto.sign_up.SignUpState
import io.golos.domain.dto.sign_up.SignUpType
import io.golos.use_cases.sign_up.core.SignUpSMCoreTransition
import io.golos.use_cases.sign_up.core.data_structs.FbSelected
import io.golos.use_cases.sign_up.core.data_structs.StartFbSignIn
import io.golos.use_cases.sign_up.dto.TransitionResult

class FromSelectingSignUpMethodOnFbSelected(
    parent: SignUpSMCoreTransition
) : SingUpTransitionBase<FbSelected>(parent, SignUpState.SELECTING_SIGN_UP_METHOD) {
    /**
     * Process event and return new state of the SM
     */
    override suspend fun process(event: FbSelected, snapshot: SignUpSnapshotDomain): TransitionResult {
        parent.sendCommand(StartFbSignIn())
        return getResult(SignUpState.WAITING_FOR_FB_TOKEN, snapshot.copy(type = SignUpType.FACEBOOK))
    }
}