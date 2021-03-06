package io.golos.cyber_android.ui.shared.mvvm

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProviders
import io.golos.cyber_android.ui.shared.helper.UIHelper
import io.golos.cyber_android.ui.shared.mvvm.model.ModelBase
import io.golos.cyber_android.ui.shared.mvvm.viewModel.FragmentViewModelFactory
import io.golos.cyber_android.ui.shared.mvvm.viewModel.ViewModelBase
import io.golos.cyber_android.ui.shared.mvvm.view_commands.ShowMessageResCommand
import io.golos.cyber_android.ui.shared.mvvm.view_commands.ShowMessageTextCommand
import io.golos.cyber_android.ui.shared.mvvm.view_commands.ViewCommand
import io.golos.domain.LogTags
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancelChildren
import timber.log.Timber
import java.util.*
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

/**
 * Base class for [DialogFragment] which support MVVM
 */
abstract class DialogBaseMVVM<VDB : ViewDataBinding, VM : ViewModelBase<out ModelBase>> : DialogFragment(), CoroutineScope {
    companion object {
        private const val INJECTION_KEY = "INJECTION_KEY"
    }

    private lateinit var injectionKey: String

    private lateinit var binding: VDB

    private lateinit var _viewModel: VM

    protected val viewModel: VM
        get() = _viewModel

    @Inject
    internal lateinit var uiHelper: UIHelper

    @Inject
    internal lateinit var viewModelFactory: FragmentViewModelFactory

    private val errorHandler = CoroutineExceptionHandler { _, exception ->
        Timber.e(exception)
    }

    override val coroutineContext: CoroutineContext = Dispatchers.Main + errorHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        injectionKey = savedInstanceState?.getString(INJECTION_KEY) ?: UUID.randomUUID().toString()
        inject(injectionKey)

        _viewModel = ViewModelProviders.of(this, viewModelFactory)[provideViewModelType()]
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString(INJECTION_KEY, injectionKey)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _viewModel.command.observe({ viewLifecycleOwner.lifecycle }) {
            if (!processViewCommandGeneral(it)) {
                processViewCommand(it)
            }
        }

        binding = DataBindingUtil.inflate(inflater, this.layoutResId(), container, false)
        binding.lifecycleOwner = this

        linkViewModel(binding, _viewModel)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        Timber.tag(LogTags.NAVIGATION).d("${javaClass.simpleName} fragment is active")
    }

    override fun onDestroyView() {
        coroutineContext.cancelChildren()
        super.onDestroyView()
    }

    override fun onDestroy() {
        if(isRemoving) {
            releaseInjection(injectionKey)
        }
        super.onDestroy()
    }

    protected fun setSelectAction(resultCode: Int, putArgsAction: Intent.() -> Unit = {}) {
        targetFragment?.onActivityResult(targetRequestCode, resultCode, Intent().also { intent ->
            putArgsAction.invoke(intent)
        })
        dismiss()
    }

    abstract fun provideViewModelType(): Class<VM>

    @LayoutRes
    protected abstract fun layoutResId(): Int

    protected abstract fun inject(key: String)

    protected abstract fun releaseInjection(key: String)

    protected abstract fun linkViewModel(binding: VDB, viewModel: VM)

    protected open fun processViewCommand(command: ViewCommand) {}

    /**
     * Process input _command
     * @return true if the _command has been processed
     */
    private fun processViewCommandGeneral(command: ViewCommand): Boolean =
        when (command) {
            is ShowMessageResCommand -> {
                uiHelper.showMessage(command.textResId, command.isError)
                true
            }
            is ShowMessageTextCommand -> {
                uiHelper.showMessage(command.text, command.isError)
                true
            }
            else -> false
        }
}