package io.golos.cyber_android.ui.shared.mvvm.view_commands

import android.net.Uri
import androidx.annotation.IdRes
import androidx.annotation.NavigationRes
import androidx.annotation.StringRes
import io.golos.cyber_android.ui.dto.Comment
import io.golos.cyber_android.ui.dto.ContentId
import io.golos.domain.dto.UserIdDomain
import io.golos.domain.use_cases.model.DiscussionIdModel

interface ViewCommand

class NavigateBackwardCommand: ViewCommand
class NavigateForwardCommand: ViewCommand

data class NavigateToCommunityPageCommand(val communityId: String) : ViewCommand

class NavigateToCommunitiesListPageCommand(val userId: UserIdDomain) : ViewCommand

class NavigateToInAppAuthScreenCommand : ViewCommand

class NavigateToMainScreenCommand : ViewCommand

class NavigationToParentScreenWithStringResultCommand(val permlink: String) : ViewCommand

class NavigateToSearchCommunitiesCommand : ViewCommand

class SetLoadingVisibilityCommand(val isVisible: Boolean) : ViewCommand

class ShowMessageResCommand(@StringRes val textResId: Int, val isError: Boolean = true): ViewCommand
class ShowMessageTextCommand(val text: String, val isError: Boolean = true): ViewCommand

class ShowPostFiltersCommand : ViewCommand

class ShowConfirmationDialog(@StringRes val textRes: Int) : ViewCommand

class HideSoftKeyboardCommand: ViewCommand

class NavigationCommand(
    @IdRes val navigationId: Int?,
    @IdRes val startDestination: Int? = null,
    @NavigationRes val graphId: Int? = null
) : ViewCommand

class NavigateToImageViewCommand(val imageUri: Uri) : ViewCommand

class NavigateToLinkViewCommand(val link: Uri) : ViewCommand

class NavigateToUserProfileCommand(val userId: UserIdDomain) : ViewCommand

class NavigateToProfileCommentMenuDialogViewCommand(val comment: Comment) : ViewCommand

class NavigateToPostCommand(
    val discussionIdModel: DiscussionIdModel,
    val contentId: ContentId
) : ViewCommand

class ShowPostRewardDialogCommand(@StringRes val titleResId: Int, @StringRes val textResId: Int) : ViewCommand

class ShowNoConnectionDialogCommand() : ViewCommand
class HideNoConnectionDialogCommand() : ViewCommand