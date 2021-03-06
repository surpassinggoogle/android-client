package io.golos.cyber_android.ui.screens.community_page.mappers

import io.golos.cyber_android.ui.screens.community_page.dto.CommunityPage
import io.golos.domain.dto.CommunityPageDomain

class CommunityPageCurrencyDomainToCommunityPageCurrency :
    Function1<CommunityPageDomain.CommunityPageCurrencyDomain, CommunityPage.CommunityPageCurrency> {

    override fun invoke(communityPageCurrencyDomain: CommunityPageDomain.CommunityPageCurrencyDomain): CommunityPage.CommunityPageCurrency {
        return CommunityPage.CommunityPageCurrency(
            communityPageCurrencyDomain.currencyName,
            communityPageCurrencyDomain.exchangeRate
        )
    }


}
