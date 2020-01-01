package io.golos.cyber_android.ui.screens.login_activity.fragments_data_pass

import io.golos.cyber_android.ui.shared.fragments_data_pass.FragmentsDataPassBase
import io.golos.cyber_android.ui.dto.QrCodeContent
import io.golos.domain.dependency_injection.scopes.ActivityScope
import javax.inject.Inject

@ActivityScope
class LoginActivityFragmentsDataPassImpl
@Inject
constructor():
    FragmentsDataPassBase(),
    LoginActivityFragmentsDataPass {

    private companion object {
        private const val QR_CODE_KEY = 22822231
    }

    override fun putQrCode(qrCode: QrCodeContent) = put(QR_CODE_KEY, qrCode)

    override fun getQrCode(): QrCodeContent? = get(QR_CODE_KEY) as QrCodeContent?
}