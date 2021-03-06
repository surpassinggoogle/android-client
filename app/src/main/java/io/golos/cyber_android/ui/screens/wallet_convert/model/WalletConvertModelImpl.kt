package io.golos.cyber_android.ui.screens.wallet_convert.model

import android.content.Context
import io.golos.cyber_android.ui.screens.wallet_convert.dto.ConvertAmountValidationResult
import io.golos.cyber_android.ui.screens.wallet_convert.model.amount_calculator.AmountCalculator
import io.golos.cyber_android.ui.screens.wallet_convert.model.amount_calculator.AmountCalculatorBrief
import io.golos.cyber_android.ui.screens.wallet_dialogs.convert_completed.ConversionCompletedInfo
import io.golos.cyber_android.ui.screens.wallet_point.dto.CarouselStartData
import io.golos.cyber_android.ui.screens.wallet_shared.amount_validator.AmountValidator
import io.golos.cyber_android.ui.screens.wallet_shared.carousel.CarouselListItem
import io.golos.cyber_android.ui.screens.wallet_shared.dto.AmountValidationResult
import io.golos.cyber_android.ui.screens.wallet_shared.getDisplayName
import io.golos.cyber_android.ui.shared.mvvm.model.ModelBaseImpl
import io.golos.data.repositories.wallet.WalletRepository
import io.golos.domain.GlobalConstants
import io.golos.domain.dependency_injection.Clarification
import io.golos.domain.dto.CommunityIdDomain
import io.golos.domain.dto.WalletCommunityBalanceRecordDomain
import io.golos.domain.repositories.GlobalSettingsRepository
import kotlinx.coroutines.flow.Flow
import java.util.*
import javax.inject.Inject
import javax.inject.Named

class WalletConvertModelImpl
@Inject
constructor(
    private val appContext: Context,
    private var currentCommunityId: CommunityIdDomain,
    @Named(Clarification.WALLET_POINT_BALANCE)
    override var balance: List<WalletCommunityBalanceRecordDomain>,
    private val _amountCalculator: AmountCalculator,
    private val amountValidator: AmountValidator,
    private val walletRepository: WalletRepository,
    private val globalSettingsRepository: GlobalSettingsRepository
) : WalletConvertModel,
    ModelBaseImpl() {

    @Suppress("JoinDeclarationAndAssignment")
    private lateinit var communBalanceRecord: WalletCommunityBalanceRecordDomain

    override var currentBalanceRecord = calculateCurrentBalanceRecord()

    override lateinit var carouselItemsData: CarouselStartData

    override var isInSellPointMode: Boolean = true

    override val isBalanceUpdated: Flow<Boolean?>
        get() =globalSettingsRepository.isBalanceUpdated

    override suspend fun notifyBalanceUpdate(isBalanceUpdated:Boolean) {
        globalSettingsRepository.notifyBalanceUpdate(isBalanceUpdated)
    }

    override val amountCalculator: AmountCalculatorBrief = _amountCalculator

    override suspend fun init() {
        _amountCalculator.init(currentBalanceRecord.points, currentBalanceRecord.communs!!, false, currentBalanceRecord.communityId)

        communBalanceRecord = balance.first { it.communityId.code == GlobalConstants.COMMUN_CODE }

        // Remove Commun record from the balance
        balance = balance.filter { it.communityId.code != GlobalConstants.COMMUN_CODE }

        carouselItemsData = CarouselStartData(
            startIndex = balance.indexOfFirst { it.communityId == currentCommunityId },
            items = balance.map { CarouselListItem(id = it.communityId, iconUrl = it.communityLogoUrl) }
        )
    }

    override fun getSellerRecord(): WalletCommunityBalanceRecordDomain =
        if(isInSellPointMode) currentBalanceRecord else communBalanceRecord

    override fun getBuyerRecord(): WalletCommunityBalanceRecordDomain =
        if(isInSellPointMode) communBalanceRecord else currentBalanceRecord

    /**
     * @return Index of the community in the balance list
     */
    override suspend fun updateCurrentCommunity(communityId: CommunityIdDomain): Int? {
        if(communityId == currentCommunityId) {
            return null
        }

        currentCommunityId = communityId
        currentBalanceRecord = calculateCurrentBalanceRecord()

        _amountCalculator.init(currentBalanceRecord.points, currentBalanceRecord.communs!!, !isInSellPointMode, currentBalanceRecord.communityId)

        return balance.indexOf(currentBalanceRecord)
    }

    override fun swipeSellMode() {
        isInSellPointMode = !isInSellPointMode
        _amountCalculator.inverse()
    }

    override fun validateAmount(): ConvertAmountValidationResult {
        val sellValidationResult = amountValidator.validate(_amountCalculator.sellAmount, getSellerRecord().points, 0.0)
//        val buyValidationResult = amountValidator.validate(_amountCalculator.buyAmount, getBuyerRecord().points, 0.0)

        return ConvertAmountValidationResult(
            sellValidationResult,
            AmountValidationResult.SUCCESS/*buyValidationResult*/,
            isValid = sellValidationResult == AmountValidationResult.SUCCESS/* && buyValidationResult == AmountValidationResult.SUCCESS*/
        )
    }

    override suspend fun convert() {
        if(isInSellPointMode) {
            walletRepository.convertPointsToCommun(_amountCalculator.sellAmount!!, getSellerRecord().communityId)
        } else {
            walletRepository.convertCommunToPoints(_amountCalculator.sellAmount!!, getBuyerRecord().communityId)
        }
    }

    override fun getConversionCompletedInfo(): ConversionCompletedInfo {
        val seller = getSellerRecord()
        val buyer = getBuyerRecord()

        return ConversionCompletedInfo(
            date = Date(),

            coins = _amountCalculator.buyAmount!!,

            sellerAvatarUrl = seller.communityLogoUrl,
            sellerName = seller.getDisplayName(appContext),
            sellerPointsTotal = seller.points - _amountCalculator.fee - _amountCalculator.sellAmount!!,

            buyerAvatarUrl = buyer.communityLogoUrl,
            buyerName = buyer.getDisplayName(appContext),
            buyerPointsTotal = buyer.points + _amountCalculator.buyAmount!!
        )
    }

    private fun calculateCurrentBalanceRecord() = balance.first { it.communityId == currentCommunityId }
}