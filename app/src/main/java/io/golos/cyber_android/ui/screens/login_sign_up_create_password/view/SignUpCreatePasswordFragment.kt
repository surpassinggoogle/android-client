package io.golos.cyber_android.ui.screens.login_sign_up_create_password.view

import android.os.Bundle
import android.view.View
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.databinding.FragmentSignUpCreatePasswordBinding
import io.golos.cyber_android.ui.screens.login_sign_up_create_password.di.SignUpCreatePasswordFragmentComponent
import io.golos.cyber_android.ui.screens.login_sign_up_create_password.view_model.SignUpCreatePasswordViewModel
import io.golos.cyber_android.ui.shared.mvvm.FragmentBaseMVVM
import io.golos.cyber_android.ui.shared.mvvm.view_commands.ViewCommand

class SignUpCreatePasswordFragment : FragmentBaseMVVM<FragmentSignUpCreatePasswordBinding, SignUpCreatePasswordViewModel>() {

    override fun provideViewModelType(): Class<SignUpCreatePasswordViewModel> = SignUpCreatePasswordViewModel::class.java

    override fun layoutResId(): Int = R.layout.fragment_sign_up_create_password

    override fun inject(key: String) = App.injections.get<SignUpCreatePasswordFragmentComponent>(key).inject(this)

    override fun releaseInjection(key: String) = App.injections.release<SignUpCreatePasswordFragmentComponent>(key)

    override fun linkViewModel(binding: FragmentSignUpCreatePasswordBinding, viewModel: SignUpCreatePasswordViewModel) {
        binding.viewModel = viewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun processViewCommand(command: ViewCommand) {
//        when(command) {
//            is NavigateToMainScreenCommand -> navigateToMainScreen()
//            is ShowBackupWarningDialogCommand -> showBackupWarningCommand()
//            is ShowSaveDialogCommand -> showSaveDialog()
//            is StartExportToGoogleDriveCommand -> keysToGoogleDriveExporter.startExport(command.fileToExport)
//            else -> throw UnsupportedOperationException("This command is not supported")
//        }
    }
}