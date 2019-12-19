package io.golos.cyber_android.ui.screens.login_sign_in_username.view_model

import android.content.Context
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dagger.Lazy
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.common.extensions.getFormattedString
import io.golos.cyber_android.ui.common.mvvm.viewModel.ViewModelBase
import io.golos.cyber_android.ui.common.mvvm.view_commands.BackCommand
import io.golos.cyber_android.ui.common.mvvm.view_commands.HideSoftKeyboardCommand
import io.golos.cyber_android.ui.common.mvvm.view_commands.ShowMessageResCommand
import io.golos.cyber_android.ui.common.mvvm.view_commands.ShowMessageTextCommand
import io.golos.cyber_android.ui.screens.login_activity.validators.password.validator.PasswordValidationResult
import io.golos.cyber_android.ui.screens.login_activity.validators.password.visializer.PasswordValidationVisualizer
import io.golos.cyber_android.ui.screens.login_activity.validators.user_name.validator.UserNameValidationResult
import io.golos.cyber_android.ui.screens.login_activity.validators.user_name.vizualizer.UserNameValidationVisualizer
import io.golos.cyber_android.ui.screens.login_sign_in_username.dto.MoveToQrCodeCommand
import io.golos.cyber_android.ui.screens.login_sign_in_username.dto.MoveToSignUpCommand
import io.golos.cyber_android.ui.screens.login_sign_in_username.dto.SetPasswordFocusCommand
import io.golos.cyber_android.ui.screens.login_sign_in_username.dto.SetUserNameFocusCommand
import io.golos.cyber_android.ui.screens.login_sign_in_username.model.SignInModel
import io.golos.domain.DispatchersProvider
import javax.inject.Inject

class SignInViewModel
@Inject
constructor(
    dispatchersProvider: DispatchersProvider,
    model: SignInModel,
    private val userNameValidationVisualizer: Lazy<UserNameValidationVisualizer>,
    private val passwordValidationVisualizer: Lazy<PasswordValidationVisualizer>,
    private val appContext: Context
) : ViewModelBase<SignInModel>(dispatchersProvider, model) {

    private val _signInButtonEnabled = MutableLiveData<Boolean>(false)
    val signInButtonEnabled: LiveData<Boolean> get() = _signInButtonEnabled

    val userName = MutableLiveData<String>("")
    val password = MutableLiveData<String>("")

    private val _pastePasswordVisibility = MutableLiveData<Int>(View.GONE)
    val pastePasswordVisibility: LiveData<Int> get() = _pastePasswordVisibility

    private val _pastePasswordText = MutableLiveData<String>()
    val pastePasswordText: LiveData<String> get() = _pastePasswordText


    val maxUserNameLen: Int
        get() = model.maxUserNameLen

    val maxPasswordLen: Int
        get() = model.maxPasswordLen

    init {
        userName.observeForever {
            _signInButtonEnabled.value = it.length >= model.minUserNameLen && password.value!!.length >= model.minPasswordLen
        }

        password.observeForever {
            _signInButtonEnabled.value = it.length >= model.minPasswordLen && userName.value!!.length >= model.minUserNameLen
        }
    }

    fun tryToGetPassFromClipboard() {
        val clipPassword = model.getPasswordFromClipboard()

        if(clipPassword != null) {
            _pastePasswordVisibility.value = View.VISIBLE
            _pastePasswordText.value = appContext.resources.getFormattedString(R.string.paste_something, clipPassword)
        } else {
            _pastePasswordVisibility.value = View.GONE
            _pastePasswordText.value = ""
        }
    }

    fun onBackButtonClick() {
        _command.value = BackCommand()
    }

    fun onPastePasswordClick() {
        val clipPassword = model.getPasswordFromClipboard()
        if(clipPassword != null) {
            password.value = clipPassword
        }
        _pastePasswordVisibility.value = View.GONE
    }

    fun onSignInClick() {
        // To prevent Done action on a soft keyboard
        if(!_signInButtonEnabled.value!!) {
            return
        }

        val userNameValidation = model.validateUserName(userName.value!!)
        if(userNameValidation != UserNameValidationResult.SUCCESS) {
            _command.value = SetUserNameFocusCommand()
            _command.value = ShowMessageTextCommand(userNameValidationVisualizer.get().toSting(userNameValidation))
            return
        }

        val passwordValidation = model.validatePassword(password.value!!)
        if(passwordValidation != PasswordValidationResult.SUCCESS) {
            _command.value = SetPasswordFocusCommand()
            _command.value = ShowMessageTextCommand(passwordValidationVisualizer.get().toSting(passwordValidation))
            return
        }

        _command.value = HideSoftKeyboardCommand()

        // Start login here
    }

    fun onMoveToSignUpClick() {
        _command.value = MoveToSignUpCommand()
    }

    fun onQrCodeClick() {
        _command.value = MoveToQrCodeCommand()
    }

    fun onQrCodeCameraPermissionsDenied() {
        _command.value = ShowMessageResCommand(R.string.qr_no_camera_permissions)
    }
}