package io.golos.cyber_android.ui.screens.login_activity.signup.fragments.name

import android.os.Bundle
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.login_activity.LoginActivityComponent
import io.golos.cyber_android.ui.common.extensions.safeNavigate
import io.golos.cyber_android.ui.screens.login_activity.signup.SignUpScreenFragmentBase
import io.golos.cyber_android.utils.asEvent
import io.golos.cyber_android.views.utils.AllLowersInputFilter
import io.golos.cyber_android.views.utils.ViewUtils
import io.golos.data.errors.AppError
import io.golos.domain.interactors.model.*
import io.golos.domain.requestmodel.QueryResult
import kotlinx.android.synthetic.main.fragment_sign_up_name.*

class SignUpNameFragment : SignUpScreenFragmentBase<SignUpNameViewModel>(SignUpNameViewModel::class.java) {
    override val fieldToValidate: EditText?
        get() = username
    override val continueButton: View
        get() = next

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_sign_up_name, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        username.filters = arrayOf(
            InputFilter.LengthFilter(SignUpNameViewModel.MAX_USERNAME_LENGTH),
            AllLowersInputFilter()
        )

        back.setOnClickListener { findNavController().navigateUp() }
        next.setOnClickListener {
            if(signUpViewModel.checkUserName(username.text.toString())) {
                signUpViewModel.updateRegisterState()
            } else {
                uiHelper.showMessage(R.string.user_name_invalid)
            }
        }

        username?.post {
            ViewUtils.showKeyboard(username)
        }
    }

    override fun observeViewModel() {
        super.observeViewModel()

        signUpViewModel.getUpdatingStateForStep<SetUserNameRequestModel>().observe(this, Observer {
            when (it) {
                is QueryResult.Loading -> showLoading()
                is QueryResult.Error -> onError(it)
            }
        })

        signUpViewModel.stateLiveData.observe(this, Observer { event ->
            event.getIfNotHandled()?.let {
                when (it) {
                    is VerifiedUserWithoutUserNameModel -> {
                        viewModel.getFieldIfValid()?.let { name ->
                            signUpViewModel.sendName(name)
                        }
                    }
                    is UnWrittenToBlockChainUserModel -> signUpViewModel.writeToBlockchain()
                    else -> { /*noop*/}

                }
            }
        })

        signUpViewModel.getUpdatingStateForStep<WriteUserToBlockChainRequestModel>().observe(this, Observer {
            when (it) {
                is QueryResult.Loading -> showLoading()
                is QueryResult.Error -> onError(it)
            }
        })

        signUpViewModel.lastRegisteredUser.asEvent().observe(this, Observer {
            onSuccess()
        })
    }

    override fun inject() = App.injections.get<LoginActivityComponent>().inject(this)

    private fun onSuccess() {
        hideLoading()
        findNavController().safeNavigate(
            R.id.signUpNameFragment,
            R.id.action_signUpNameFragment_to_signUpKeyFragment
        )
    }

    private fun onError(errorResult: QueryResult.Error<NextRegistrationStepRequestModel>) {
        hideLoading()
        val errorMsg = when (errorResult.error) {
            is AppError.NameIsAlreadyInUseError -> R.string.name_already_taken_error
            else -> R.string.unknown_error
        }
        Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_SHORT).show()
    }
}
