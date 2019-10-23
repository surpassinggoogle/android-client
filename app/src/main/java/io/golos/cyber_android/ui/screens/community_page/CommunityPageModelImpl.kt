package io.golos.cyber_android.ui.screens.community_page

import io.golos.cyber_android.ui.common.mvvm.model.ModelBaseImpl
import io.golos.domain.interactors.community.GetCommunityPageByIdUseCase
import javax.inject.Inject

class CommunityPageModelImpl @Inject constructor(communityPageByIdUseCase: GetCommunityPageByIdUseCase) :
    ModelBaseImpl(),
    CommunityPageModel,
    GetCommunityPageByIdUseCase by communityPageByIdUseCase{


}