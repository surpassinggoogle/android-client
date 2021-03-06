package io.golos.cyber_android.ui.screens.in_app_auth_activity.fragments.pin_code

import android.os.Bundle
import android.view.View
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.databinding.FragmentPinCodeAuthBinding
import io.golos.cyber_android.ui.screens.in_app_auth_activity.InAppAuthActivity
import io.golos.cyber_android.ui.screens.in_app_auth_activity.fragments.pin_code.di.PinCodeAuthFragmentComponent
import io.golos.cyber_android.ui.screens.in_app_auth_activity.navigation.Navigator
import io.golos.cyber_android.ui.screens.in_app_auth_activity.view_commands.AuthSuccessCommand
import io.golos.cyber_android.ui.screens.in_app_auth_activity.view_commands.ResetPinCommand
import io.golos.cyber_android.ui.screens.in_app_auth_activity.view_commands.SetPinCodeDigitCommand
import io.golos.cyber_android.ui.screens.in_app_auth_activity.view_commands.SwitchToFingerprintCommand
import io.golos.cyber_android.ui.shared.mvvm.FragmentBaseMVVM
import io.golos.cyber_android.ui.shared.mvvm.view_commands.ViewCommand
import kotlinx.android.synthetic.main.fragment_pin_code_auth.*
import javax.inject.Inject

/**
 * Fragment for authentication via PIN code
 */
class PinCodeAuthFragment : FragmentBaseMVVM<FragmentPinCodeAuthBinding, PinCodeAuthViewModel>() {

    @Inject
    internal lateinit var navigator: Navigator

    override fun provideViewModelType(): Class<PinCodeAuthViewModel> = PinCodeAuthViewModel::class.java

    override fun layoutResId(): Int = R.layout.fragment_pin_code_auth

    override fun inject(key: String) = App.injections
        .get<PinCodeAuthFragmentComponent>(key, arguments!!.getInt(InAppAuthActivity.PIN_CODE_HEADER_ID))
        .inject(this)

    override fun releaseInjection(key: String) = App.injections.release<PinCodeAuthFragmentComponent>(key)

    override fun linkViewModel(binding: FragmentPinCodeAuthBinding, viewModel: PinCodeAuthViewModel) {
        binding.viewModel = viewModel
    }

    override fun processViewCommand(command: ViewCommand) {
        when(command) {
            is SwitchToFingerprintCommand -> navigator.moveToFingerprint(this)
            is SetPinCodeDigitCommand -> pinCode.setDigit(command.digit)
            is AuthSuccessCommand -> navigator.processAuthSuccess(requireActivity())
            is ResetPinCommand -> pinCode.reset()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        keypad.setOnDigitKeyPressListener { viewModel.onDigitPressed(it) }
        keypad.setOnClearKeyPressListener { viewModel.onClearKeyPressed() }
    }

    override fun onPause() {
        super.onPause()
        viewModel.onInactive()
    }
}