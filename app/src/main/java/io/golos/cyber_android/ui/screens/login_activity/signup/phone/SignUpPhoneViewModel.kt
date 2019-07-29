package io.golos.cyber_android.ui.screens.login_activity.signup.phone

import io.golos.cyber_android.ui.screens.login_activity.signup.SignUpScreenViewModelBase

class SignUpPhoneViewModel: SignUpScreenViewModelBase() {
    override fun validate(field: String): Boolean {
        return field.length > 10
    }
}