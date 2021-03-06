package io.golos.cyber_android.ui.screens.app_start.welcome.activity.model

import io.golos.cyber_android.ui.shared.mvvm.model.ModelBaseImpl
import io.golos.data.network_state.NetworkStateChecker
import io.golos.data.repositories.settings.SettingsRepository
import io.golos.domain.DispatchersProvider
import io.golos.domain.KeyValueStorageFacade
import io.golos.domain.UserKeyStore
import io.golos.domain.dto.AuthStateDomain
import io.golos.domain.dto.AuthType
import io.golos.domain.dto.UserKeyType
import io.golos.domain.repositories.CurrentUserRepository
import io.golos.use_cases.auth.AuthUseCase
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import kotlin.system.exitProcess

class WelcomeModelImpl
@Inject
constructor(
    private val dispatchersProvider: DispatchersProvider,
    private val authUseCase: AuthUseCase,
    private val userKeyStore: UserKeyStore,
    private val keyValueStorage: KeyValueStorageFacade,
    private val networkStateChecker: NetworkStateChecker,
    private val currentUserRepository: CurrentUserRepository,
    private val settingsRepository: SettingsRepository
) : ModelBaseImpl(),
    WelcomeModel {

    private lateinit var authState: AuthStateDomain

    override val hasNetworkConnection: Boolean
        get() = networkStateChecker.isConnected

    override suspend fun hasAuthState(): Boolean =
        withContext(dispatchersProvider.ioDispatcher) {
            val isAuthStateSavedFromNativeApp = keyValueStorage.isAuthStateSavedFromNativeApp()

            keyValueStorage.getAuthState()
                ?.let {
                    // Data correction for compatibility with WebView users
                    var correctedState = it
                    if(it.isKeysExported && !isAuthStateSavedFromNativeApp) {
                        correctedState = it.copy(isKeysExported = false, type = AuthType.SIGN_UP)
                        keyValueStorage.saveAuthState(correctedState)
                    }

                    authState = correctedState
                    currentUserRepository.authState = correctedState
                    true
                } ?: false
        }

    override fun closeApp() = exitProcess(0)

    override suspend fun login() =
        try {
            val masterPassword = withContext(dispatchersProvider.ioDispatcher) {
                userKeyStore.getKey(UserKeyType.MASTER)
            }

            authUseCase.auth(authState.userName, masterPassword)
        } catch (ex: Exception) {
            Timber.e(ex)
            throw ex
        }

    override suspend fun isOutdated(): Boolean {
        val config = settingsRepository.getConfig()
        return config.isNeedAppUpdate
    }
}