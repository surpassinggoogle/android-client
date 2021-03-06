package io.golos.cyber_android.ui.screens.wallet.view_model

import android.content.Context
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.screens.wallet.data.enums.Currencies
import io.golos.cyber_android.ui.screens.wallet.dto.*
import io.golos.cyber_android.ui.screens.wallet.model.CurrencyBalance
import io.golos.cyber_android.ui.screens.wallet.model.WalletModel
import io.golos.cyber_android.ui.screens.wallet.view.my_points.WalletMyPointsListItemEventsProcessor
import io.golos.cyber_android.ui.screens.wallet_shared.history.view.WalletHistoryListItemEventsProcessor
import io.golos.cyber_android.ui.screens.wallet_shared.send_points.list.view.WalletSendPointsListItemEventsProcessor
import io.golos.cyber_android.ui.shared.extensions.getMessage
import io.golos.cyber_android.ui.shared.mvvm.viewModel.ViewModelBase
import io.golos.cyber_android.ui.shared.mvvm.view_commands.NavigateBackwardCommand
import io.golos.cyber_android.ui.shared.mvvm.view_commands.ShowMessageTextCommand
import io.golos.cyber_android.ui.shared.recycler_view.versioned.LoadingListItem
import io.golos.cyber_android.ui.shared.recycler_view.versioned.NoDataListItem
import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem
import io.golos.domain.DispatchersProvider
import io.golos.domain.GlobalConstants
import io.golos.domain.dto.CommunityIdDomain
import io.golos.domain.dto.HistoryFilterDomain
import io.golos.domain.dto.UserBriefDomain
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class WalletViewModel
@Inject
constructor(
    private val appContext: Context,
    dispatchersProvider: DispatchersProvider,
    model: WalletModel
) : ViewModelBase<WalletModel>(
    dispatchersProvider,
    model
), WalletMyPointsListItemEventsProcessor,
    WalletSendPointsListItemEventsProcessor,
    WalletHistoryListItemEventsProcessor {

    private val _swipeRefreshing = MutableLiveData<Boolean>(false)
    val swipeRefreshing: LiveData<Boolean> get() = _swipeRefreshing

    private val _totalValue = MutableLiveData<Double>(0.0)
    val totalValue: LiveData<Double> = _totalValue

    private val _currencyBalance = MutableLiveData(CurrencyBalance(0.0, Currencies.COMMUN))
    val currencyBalance: LiveData<CurrencyBalance> = _currencyBalance

    private val _collapsedPanelTitle = MutableLiveData<String>(appContext.resources.getString(R.string.profile_wallet_title))
    val collapsedPanelTitle: LiveData<String> = _collapsedPanelTitle

    private val _myPointsItems = MutableLiveData<List<MyPointsListItem>>(listOf())
    val myPointsItems: LiveData<List<MyPointsListItem>> = _myPointsItems

    val sendPointItems: LiveData<List<VersionedListItem>> = model.sendPointItems

    private val _sendPointsVisibility = MutableLiveData<Int>(View.GONE)
    val sendPointsVisibility: LiveData<Int> = _sendPointsVisibility

    val historyItems: LiveData<List<VersionedListItem>> = model.historyItems

    val pageSize = model.pageSize

    private var loadPageJob: Job? = null

    init {
        sendPointItems.observeForever {
            _sendPointsVisibility.value = if (isSendPointsListEmpty(it)) View.GONE else View.VISIBLE
        }

        loadPage(false)

        launch {
            model.isBalanceUpdated.collect {
                it?.let {
                    loadPage(true)
                    model.clearBalanceUpdateLastCallback()
                }
            }
        }
    }

    fun onSwipeRefresh() = loadPage(true)

    fun onBackClick() {
        _command.value = NavigateBackwardCommand()
    }

    fun onUsdClick() {
        launch {
            model.saveBalanceCurrency(Currencies.USD)
            _currencyBalance.value = CurrencyBalance(model.totalBalance, model.balanceCurrency)
            model.notifyCurrencyUpdate(true)
        }
    }

    fun onCommunClick() {
        launch {
            model.saveBalanceCurrency(Currencies.COMMUN)
            _currencyBalance.value = CurrencyBalance(model.totalBalance, model.balanceCurrency)
            model.notifyCurrencyUpdate(true)
        }
    }

    fun onSettingsClick() {
        _command.value = ShowSettingsDialog(model.getEmptyBalanceVisibility())
    }

    fun onSeeAllMyPointsClick() {
        val balanceItems = if(model.getEmptyBalanceVisibility())
            model.balance.filter { it.points > 0 }.sortedByDescending { it.points }
        else
            model.balance.sortedByDescending { it.points }

        val communIndex = balanceItems.indexOfFirst { it.communityId.code == GlobalConstants.COMMUN_CODE }
        if(communIndex != -1) {
            val mutableItems = balanceItems.toMutableList()
            val communItem = balanceItems[communIndex]
            mutableItems.removeAt(communIndex)
            mutableItems.add(0, communItem)

            _command.value = ShowMyPointsDialog(mutableItems)
        } else {
            _command.value = ShowMyPointsDialog(balanceItems)
        }
    }

    fun onSeeAllSendPointsClick() {
        _command.value = ShowSendPointsDialog()
    }

    override fun onSendPointsNextPageReached() {
        launch {
            model.loadSendPointsPage()
        }
    }

    override fun onSendPointsRetryClick() {
        launch {
            model.retrySendPointsPage()
        }
    }

    override fun onHistoryNextPageReached() {
        launch {
            model.loadHistoryPage()
        }
    }

    override fun onHistoryRetryClick() {
        launch {
            model.retryHistoryPage()
        }
    }

    override fun onFilterClick() {
        _command.postValue(ShowFilterDialog())
    }

    override fun onMyPointItemClick(communityId: CommunityIdDomain) {
        if (communityId.code == GlobalConstants.COMMUN_CODE) {
            return
        }

        _command.value = NavigateToWalletPoint(communityId, model.balance)
    }

    override fun onSendPointsItemClick(user: UserBriefDomain?) {
        model.balance.let {
            _command.value = NavigateToWalletSendPoints(it.first().communityId, user, it)
        }
    }

    fun onEmptyBalancesShowHide(isVisible: Boolean) {
        launch {
            model.toggleShowHideEmptyBalances(isVisible)
            setupPointsItems()
        }
    }

    fun onConvertClick() {
        model.balance.let { balance ->
            _command.value =
                NavigateToWalletConvertCommand(balance.first { it.communityId.code != GlobalConstants.COMMUN_CODE }.communityId, balance)
        }
    }

    private fun loadPage(needReload: Boolean) {
        loadPageJob?.cancel()
        loadPageJob = launch {
            try {
                if (needReload) {
                    model.clearSendPoints()
                    model.clearHistory()
                }

                model.initBalance(needReload)

                _totalValue.value = model.totalBalance
                setupPointsItems()

                _currencyBalance.value = CurrencyBalance(model.totalBalance, model.balanceCurrency)

                // To load the very first page
                onSendPointsNextPageReached()
                onHistoryNextPageReached()
            } catch (ex: Exception) {
                Timber.e(ex)
                _command.value = ShowMessageTextCommand(ex.getMessage(appContext))

                _command.value = NavigateBackwardCommand()
            } finally {
                if (needReload) {
                    _swipeRefreshing.value = false
                }
            }
        }
    }

    private suspend fun setupPointsItems() {
        val items = if(model.getEmptyBalanceVisibility())
            model.getMyPointsItems().filter { it.data.points > 0 }.sortedByDescending { it.data.points }
        else
            model.getMyPointsItems().sortedByDescending { it.data.points }

        val communIndex = items.indexOfFirst { it.isCommun }
        if(communIndex != -1) {
            val mutableItems = items.toMutableList()
            val communItem = items[communIndex]
            mutableItems.removeAt(communIndex)
            mutableItems.add(0, communItem)

            _myPointsItems.value = mutableItems
        } else {
            _myPointsItems.value = items
        }
    }

    private fun isSendPointsListEmpty(items: List<VersionedListItem>) =
        items.isEmpty() || (items.size == 1 && (items[0] is NoDataListItem || items[0] is LoadingListItem))

    fun applyFilters(historyFilterDomain: HistoryFilterDomain) {
        launch {
            try {
                model.applyFilters(historyFilterDomain)
            }catch (e:Exception){
                Timber.e(e)
                _command.value = ShowMessageTextCommand(e.getMessage(appContext))
                _command.value = NavigateBackwardCommand()
            }
        }
    }

    fun getCurrentFilter() = model.getCurrentFilter()
}