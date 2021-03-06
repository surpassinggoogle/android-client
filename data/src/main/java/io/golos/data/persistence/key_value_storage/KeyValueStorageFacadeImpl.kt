package io.golos.data.persistence.key_value_storage

import com.squareup.moshi.Moshi
import io.golos.commun4j.sharedmodel.CyberName
import io.golos.data.persistence.key_value_storage.storages.Storage
import io.golos.domain.KeyValueStorageFacade
import io.golos.domain.dto.*
import io.golos.domain.dto.sign_up.SignUpSnapshotDomain
import javax.inject.Inject

/**
 * Helper class for access to App-level private shared preferences
 */
class KeyValueStorageFacadeImpl
@Inject
constructor(
    private val keyValueStorage: Storage,
    private val moshi: Moshi
) : KeyValueStorageFacade {

    private companion object {
        private const val CRYPTO_KEY_AES_KEY = "CRYPTO_KEY_AES"

        private const val AUTH_STATE_KEY = "AUTH_STATE"
        private const val AUTH_STATE_NATIVE_KEY = "AUTH_STATE_NATIVE"

        private const val UI_MODE = "UI_MODE"

        private const val PIN_CODE_KEY = "PIN_CODE"
        private const val APP_UNLOCK_WAY_KEY = "APP_UNLOCK_WAY"

        private const val KEY_FTUE_BOARD_STAGE_KEY = "KEY_FTUE_BOARD_STAGE"
        private const val KEY_FTUE_COMMUNITY_SUBSCRIPTIONS_KEY = "KEY_FTUE_COMMUNITY_SUBSCRIPTIONS"

        private const val USER_KEY_TYPE_POSTING_KEY =  "USER_KEY_TYPE_POSTING"
        private const val USER_KEY_TYPE_OWNER_KEY = "USER_KEY_TYPE_OWNER"
        private const val USER_KEY_TYPE_MEMO_KEY = "USER_KEY_TYPE_MEMO"
        private const val USER_KEY_TYPE_ACTIVE_KEY = "USER_KEY_TYPE_ACTIVE"
        private const val USER_KEY_TYPE_MASTER_KEY = "USER_KEY_TYPE_MASTER"

        private const val FCM_TOKEN_KEY = "FCM_TOKEN"
        private const val SIGN_UP_SNAPSHOT_KEY = "SIGN_UP_SNAPSHOT"

        private const val EMPTY_BALANCE_VISIBILITY_KEY = "EMPTY_BALANCE_VISIBILITY"

        private const val CURRENCY_COEFFICIENT_KEY = "CURRENCY_COEFFICIENT"

        private const val DISPLAYED_REWARD_CURRENCY_KEY = "DISPLAYED_REWARD_CURRENCY"
    }

    // To provide compatibility to WebView application
    // We must remove it in a future
    private data class OldWebAuthState(
        val userName: String,
        val user: CyberName,
        val isUserLoggedIn: Boolean,
        val isPinCodeSettingsPassed: Boolean,
        val isFingerprintSettingsPassed: Boolean,
        val isKeysExported: Boolean,
        val type: AuthType
    )

    /**
     * Save encrypted key for AES encoding (key is encrypted via RSA alg, it works for old devices only - prior to 23 API)
     */
    override fun saveAESCryptoKey(key: ByteArray) =
        keyValueStorage.update {
            it.putBytes(CRYPTO_KEY_AES_KEY, key)
        }

    /**
     * Get encrypted key for AES encoding (key is encrypted via RSA alg, it works for old devices only - prior to 23 API)
     */
    override fun getAESCryptoKey(): ByteArray? =
        keyValueStorage.read {
            it.readBytes(CRYPTO_KEY_AES_KEY)
        }

    override fun saveAuthState(state: AuthStateDomain) =
        keyValueStorage.update {
            it.putString(AUTH_STATE_KEY, moshi.adapter(AuthStateDomain::class.java).toJson(state))
            it.putBoolean(AUTH_STATE_NATIVE_KEY, true)
        }

    override fun getAuthState(): AuthStateDomain? =
        keyValueStorage.read {
            val authStateString = it.readString(AUTH_STATE_KEY) ?: return@read null
            var result = moshi.adapter(AuthStateDomain::class.java).fromJson(authStateString)

            // To provide compatibility to WebView application
            @Suppress("SENSELESS_COMPARISON")
            if(result!!.user.userId == null) {
                val oldResult = moshi.adapter(OldWebAuthState::class.java).fromJson(authStateString)
                result = result.copy(user = UserIdDomain(oldResult!!.user.name))
            }

            return@read result
        }

    override fun removeAuthState() =
        keyValueStorage.update {
            it.remove(AUTH_STATE_KEY)
        }

    override fun isAuthStateSavedFromNativeApp() =
        keyValueStorage.read {
            it.readBoolean(AUTH_STATE_NATIVE_KEY) ?: false
        }

    override fun saveUserKey(key: ByteArray, keyType: UserKeyType) =
        keyValueStorage.update {
            it.putBytes(getInternalKeyForUserKey(keyType), key)
        }

    override fun getUserKey(keyType: UserKeyType): ByteArray? =
        keyValueStorage.read {
            it.readBytes(getInternalKeyForUserKey(keyType))
        }

    override fun removeUserKey(keyType: UserKeyType) =
        keyValueStorage.update {
            it.remove(getInternalKeyForUserKey(keyType))
        }

    override fun savePinCode(pinCode: ByteArray) =
        keyValueStorage.update {
            it.putBytes(PIN_CODE_KEY, pinCode)
        }

    override fun getPinCode(): ByteArray? =
        keyValueStorage.read {
            it.readBytes(PIN_CODE_KEY)
        }

    override fun removePinCode() =
        keyValueStorage.update {
            it.remove(PIN_CODE_KEY)
        }

    override fun saveAppUnlockWay(unlockWay: AppUnlockWay) =
        keyValueStorage.update {
            it.putInt(APP_UNLOCK_WAY_KEY, unlockWay.value)
        }

    override fun getAppUnlockWay(): AppUnlockWay? =
        keyValueStorage.read {
            it.readInt(APP_UNLOCK_WAY_KEY)?.let { value -> AppUnlockWay.createFromValue(value) }
        }

    override fun removeAppUnlockWay() =
        keyValueStorage.update {
            it.remove(APP_UNLOCK_WAY_KEY)
        }

    override fun saveFtueBoardStage(stage: FtueBoardStageEntity) =
        keyValueStorage.update {
            it.putString(KEY_FTUE_BOARD_STAGE_KEY, stage.name)
        }

    override fun getFtueBoardStage(): FtueBoardStageEntity =
        keyValueStorage.read {
            FtueBoardStageEntity.valueOf(it.readString(KEY_FTUE_BOARD_STAGE_KEY) ?: FtueBoardStageEntity.IDLE.name)
        }

    override fun saveFtueCommunitySubscriptions(communitySubscriptions: List<CommunityEntity>) {
        keyValueStorage.update {
            val communitySubscriptionsEntity =
                CommunitySubscriptionsEntity(communitySubscriptions)
            val adapter = moshi.adapter(CommunitySubscriptionsEntity::class.java)
            val json = adapter.toJson(communitySubscriptionsEntity)
            it.putString(KEY_FTUE_COMMUNITY_SUBSCRIPTIONS_KEY, json)
        }
    }

    override fun getFtueCommunitySubscriptions(): List<CommunityEntity> =
        keyValueStorage.read { storage ->
            val communitySubscriptionsJson = storage.readString(KEY_FTUE_COMMUNITY_SUBSCRIPTIONS_KEY)
            val adapter = moshi.adapter(CommunitySubscriptionsEntity::class.java)
            communitySubscriptionsJson?.let { adapter.fromJson(it)?.communities } ?: listOf()
        }

    override fun removeFtueState() =
        keyValueStorage.update {
            it.remove(KEY_FTUE_BOARD_STAGE_KEY)
            it.remove(KEY_FTUE_COMMUNITY_SUBSCRIPTIONS_KEY)
        }

    override fun saveFcmToken(token: FcmTokenStateDomain) =
        keyValueStorage.update {
            it.putString(FCM_TOKEN_KEY, moshi.adapter(FcmTokenStateDomain::class.java).toJson(token))
        }

    override fun getFcmToken(): FcmTokenStateDomain? =
        keyValueStorage.read {
            it.readString(FCM_TOKEN_KEY)?.let { moshi.adapter(FcmTokenStateDomain::class.java).fromJson(it) }
        }

    override fun saveSignUpSnapshot(snapshot: SignUpSnapshotDomain) =
        keyValueStorage.update {
            it.putString(SIGN_UP_SNAPSHOT_KEY, moshi.adapter(SignUpSnapshotDomain::class.java).toJson(snapshot))
        }

    override fun getSignUpSnapshot(): SignUpSnapshotDomain? =
        keyValueStorage.read {
            it.readString(SIGN_UP_SNAPSHOT_KEY)?.let { moshi.adapter(SignUpSnapshotDomain::class.java).fromJson(it) }
        }

    override fun removeSignUpSnapshot() =
        keyValueStorage.update {
            it.remove(SIGN_UP_SNAPSHOT_KEY)
        }

    override fun saveDisplayedRewardCurrency(currency: RewardCurrency) {
        keyValueStorage.update {
            it.putString(DISPLAYED_REWARD_CURRENCY_KEY, currency.toString())
        }
    }

    @Suppress("NestedLambdaShadowedImplicitParameter")
    override fun getDisplayedRewardCurrency(): RewardCurrency =
        keyValueStorage.read {
            it.readString(DISPLAYED_REWARD_CURRENCY_KEY)?.let { RewardCurrency.createFrom(it) } ?: RewardCurrency.POINTS
        }

    override fun saveEmptyBalancesVisibility(areEmptyBalancesVisible: Boolean) {
        keyValueStorage.update {
            it.putBoolean(EMPTY_BALANCE_VISIBILITY_KEY,areEmptyBalancesVisible)
        }
    }

    override fun areEmptyBalancesVisibility(): Boolean {
        return keyValueStorage.read {
            it.readBoolean(EMPTY_BALANCE_VISIBILITY_KEY)
        } ?: false
    }

    override fun saveCurrencyCoefficient(currencyName:String) {
        keyValueStorage.update {
            it.putString(CURRENCY_COEFFICIENT_KEY,currencyName)
        }
    }

    override fun getCurrencyCoefficient(): String? {
        return keyValueStorage.read {
            it.readString(CURRENCY_COEFFICIENT_KEY)
        }
    }

    override fun getUIMode(): String? {
        return keyValueStorage.read {
            it.readString(UI_MODE)
        }
    }

    override fun setUIMode(uiMode: String) {
        keyValueStorage.update {
            it.putString(UI_MODE,uiMode)
        }
    }

    private fun getInternalKeyForUserKey(keyType: UserKeyType) =
        when(keyType) {
            UserKeyType.POSTING -> USER_KEY_TYPE_POSTING_KEY
            UserKeyType.OWNER -> USER_KEY_TYPE_OWNER_KEY
            UserKeyType.MEMO -> USER_KEY_TYPE_MEMO_KEY
            UserKeyType.ACTIVE -> USER_KEY_TYPE_ACTIVE_KEY
            UserKeyType.MASTER -> USER_KEY_TYPE_MASTER_KEY
        }
}