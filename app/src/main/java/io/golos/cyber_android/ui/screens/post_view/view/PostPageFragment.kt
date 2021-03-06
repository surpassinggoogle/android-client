package io.golos.cyber_android.ui.screens.post_view.view

import android.animation.AnimatorInflater
import android.animation.StateListAnimator
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import androidx.annotation.StringRes
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.databinding.FragmentPostBinding
import io.golos.cyber_android.ui.dialogs.CommentsActionsDialog
import io.golos.cyber_android.ui.dialogs.ConfirmationDialog
import io.golos.cyber_android.ui.dialogs.PostRewardBottomSheetDialog
import io.golos.cyber_android.ui.dialogs.SelectRewardCurrencyDialog
import io.golos.cyber_android.ui.dialogs.donation.DonationUsersDialog
import io.golos.cyber_android.ui.dto.Post
import io.golos.cyber_android.ui.dto.ProfileItem
import io.golos.cyber_android.ui.screens.community_page.view.CommunityPageFragment
import io.golos.cyber_android.ui.screens.donate_send_points.view.DonateSendPointsFragment
import io.golos.cyber_android.ui.screens.post_edit.activity.EditorPageActivity
import io.golos.cyber_android.ui.screens.post_edit.fragment.view.EditorPageFragment
import io.golos.cyber_android.ui.screens.post_page_menu.model.PostMenu
import io.golos.cyber_android.ui.screens.post_page_menu.view.PostPageMenuDialog
import io.golos.cyber_android.ui.screens.post_report.view.PostReportDialog
import io.golos.cyber_android.ui.screens.post_view.di.PostPageFragmentComponent
import io.golos.cyber_android.ui.screens.post_view.dto.*
import io.golos.cyber_android.ui.screens.post_view.dto.post_list_items.PostControlsListItem
import io.golos.cyber_android.ui.screens.post_view.view.list.PostPageAdapter
import io.golos.cyber_android.ui.screens.post_view.view_model.PostPageViewModel
import io.golos.cyber_android.ui.screens.profile.view.ProfileExternalUserFragment
import io.golos.cyber_android.ui.screens.profile_photos.view.ProfilePhotosFragment
import io.golos.cyber_android.ui.shared.Tags
import io.golos.cyber_android.ui.shared.Tags.MENU
import io.golos.cyber_android.ui.shared.extensions.reduceDragSensitivity
import io.golos.cyber_android.ui.shared.mvvm.FragmentBaseMVVM
import io.golos.cyber_android.ui.shared.mvvm.view_commands.*
import io.golos.cyber_android.ui.shared.utils.hideSoftKeyboard
import io.golos.cyber_android.ui.shared.utils.openImageView
import io.golos.cyber_android.ui.shared.utils.openLinkView
import io.golos.cyber_android.ui.shared.utils.shareMessage
import io.golos.domain.commun_entities.Permlink
import io.golos.domain.dto.CommunityIdDomain
import io.golos.domain.dto.ContentIdDomain
import io.golos.domain.dto.RewardCurrency
import io.golos.domain.dto.UserIdDomain
import io.golos.domain.use_cases.model.DiscussionIdModel
import io.golos.domain.use_cases.model.PostModel
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.fragment_post.*

/**
 * Fragment for single [PostModel] presentation
 */
class PostPageFragment : FragmentBaseMVVM<FragmentPostBinding, PostPageViewModel>() {
    companion object {
        private const val REQUEST_FOR_RESULT_FROM_EDIT = 41242

        const val UPDATED_REQUEST_CODE = 41245

        fun newInstance(args: Args, callback: (postControlsListItem:PostControlsListItem) -> Unit): PostPageFragment {
            return PostPageFragment().apply {
                this.callback = callback
                arguments = Bundle().apply {
                    putParcelable(Tags.ARGS, args)
                }
                setTargetFragment(targetFragment, UPDATED_REQUEST_CODE)
            }
        }
    }

    @Parcelize
    data class Args(val id: DiscussionIdModel, val contentId: ContentIdDomain? = null, val scrollToComments: Boolean = false) : Parcelable

    private var callback: ((postControlsListItem:PostControlsListItem) -> Unit)? = null
    private val topShadow: StateListAnimator by lazy { AnimatorInflater.loadStateListAnimator(context, R.animator.appbar_elevation) }
    private var isTopShadowSet = false
    private val topShadowThreshold by lazy { context!!.resources.getDimension(R.dimen.post_top_shadow_threshold) }
    private var oldStateListAnimator: StateListAnimator? = null
    private var postControlsListItem: PostControlsListItem?=null
    override fun provideViewModelType(): Class<PostPageViewModel> = PostPageViewModel::class.java

    override fun layoutResId(): Int = R.layout.fragment_post

    override fun inject(key: String) =
        App.injections.get<PostPageFragmentComponent>(key, arguments!!.getParcelable<Args>(Tags.ARGS)!!.id, arguments!!.getParcelable<Args>(Tags.ARGS)!!.contentId).inject(this)

    override fun linkViewModel(binding: FragmentPostBinding, viewModel: PostPageViewModel) {
        binding.viewModel = viewModel
    }

    override fun releaseInjection(key: String) = App.injections.release<PostPageFragmentComponent>(key)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        oldStateListAnimator = actionBar.stateListAnimator

        viewModel.post.observe(viewLifecycleOwner, Observer {
            it.forEach {item->
                if (item is PostControlsListItem)
                    postControlsListItem = item
            }

            (postView.adapter as PostPageAdapter).update(it)
        })

        viewModel.title.observe(viewLifecycleOwner, Observer {

        })

        postView.reduceDragSensitivity()

        postHeader.setOnBackButtonClickListener {

            back()
        }
        postHeader.setOnMenuButtonClickListener { viewModel.onPostMenuClick() }
        postHeader.setOnRewardButtonClickListener { viewModel.onPostRewardClick() }
        postHeader.setOnUserClickListener { viewModel.onUserInHeaderClick(it) }
        postHeader.setOnCommunityClickListener { communityId -> viewModel.onCommunityClicked(communityId) }

        commentWidget.onSendClickListener = { comment ->
            viewModel.sendComment(comment)
        }

        commentWidget.onAttachImageListener = { attachmentUrl ->
            openSelectPhotoView(attachmentUrl)
        }

        // Setup the list
        val adapter = PostPageAdapter(viewModel, viewModel.commentsPageSize)
        adapter.setHasStableIds(true)
        postView.adapter = adapter

        val postListLayoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        postView.layoutManager = postListLayoutManager
        (postView.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false

        postView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val scrollOffset = postView.computeVerticalScrollOffset()

                if (scrollOffset < topShadowThreshold) {
                    if (isTopShadowSet) {
                        actionBar.stateListAnimator = oldStateListAnimator      // Remove shadow
                        isTopShadowSet = false
                    }
                } else {
                    if (!isTopShadowSet) {
                        actionBar.stateListAnimator = topShadow                 // Add shadow
                        isTopShadowSet = true
                    }
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()
        viewModel.setup()
    }

    override fun processViewCommand(command: ViewCommand) {
        when (command) {
            is NavigateBackwardCommand -> getDashboardFragment(this)?.navigateBack(null)

            is NavigationToParentScreenWithStringResultCommand -> {
                val permlink = command.permlink
                setSelectAction(UPDATED_REQUEST_CODE) {
                    putExtra(Tags.PERMLINK_EXTRA, permlink)
                }
                back()
            }

            is NavigateToImageViewCommand -> requireContext().openImageView(command.imageUri)
            is NavigateToLinkViewCommand -> requireContext().openLinkView(command.link)
            is NavigateToUserProfileCommand -> openUserProfile(command.userId)
            is NavigateToCommunityPageCommand -> openCommunityPage(command.communityId)
            is NavigationToEditPostViewCommand -> openEditPost(command.contentId)
            is NavigateToEditComment -> commentWidget.setCommentForEdit(command.contentId, command.body)
            is NavigationToPostMenuViewCommand -> openPostMenuDialog(command.postMenu)
            is ClearCommentInputCommand -> commentWidget.clear()
            is ShowCommentMenuViewCommand -> showCommentMenu(command.commentId)
            is SharePostCommand -> sharePost(command.shareUrl)
            is ViewInExplorerViewCommand -> viewInExplorer(command.exploreUrl)
            is ReportPostCommand -> showReportPost(command.contentId)
            is DeletePostCommand -> deletePost()
            is NavigateToReplyCommentViewCommand -> commentWidget.setCommentForReply(command.contentId, command.body)
            is ShowPostRewardDialogCommand -> showPostRewardDialog(command.titleResId, command.textResId)
            is NavigateToDonateCommand -> moveToDonate(command)
            is ShowDonationUsersDialogCommand -> showDonationUsersDialogCommand(command.post)
            is SelectRewardCurrencyDialogCommand -> showSelectRewardCurrencyDialog(command.startCurrency)
            else -> throw UnsupportedOperationException("This command is not supported")
        }
    }

    private fun viewInExplorer(exploreUrl: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(exploreUrl))
        startActivity(intent)
    }

    private fun back() {
        postControlsListItem?.let {
            callback!!.invoke(it)
        }

        view?.hideSoftKeyboard(0)
        parentFragmentManager.popBackStack()

    }

    private fun openSelectPhotoView(imageUrl: String?) {
        getDashboardFragment(this)?.navigateToFragment(ProfilePhotosFragment.newInstance(ProfileItem.COMMENT, imageUrl, this@PostPageFragment))
    }

    private fun openUserProfile(userId: UserIdDomain) {
        getDashboardFragment(this)?.navigateToFragment(ProfileExternalUserFragment.newInstance(userId))
    }

    private fun openCommunityPage(communityId: CommunityIdDomain) {
        getDashboardFragment(this)?.navigateToFragment(CommunityPageFragment.newInstance(communityId))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            ProfilePhotosFragment.REQUEST -> {
                val result = data?.extras?.getParcelable<ProfilePhotosFragment.Result>(ProfilePhotosFragment.RESULT)
                commentWidget.updateImageAttachment(result?.photoFilePath)
            }

            ConfirmationDialog.REQUEST -> {
                if (resultCode == ConfirmationDialog.RESULT_OK) {
                    viewModel.deletePost()
                }
            }

            REQUEST_FOR_RESULT_FROM_EDIT -> {
                when (resultCode) {
                    Activity.RESULT_OK -> {
                        data?.action?.let { action ->
                            when (action) {
                                Tags.ACTION_EDIT_SUCCESS -> {
                                    val contentId = data.getParcelableExtra<ContentIdDomain>(Tags.CONTENT_ID)
                                    val discussionIdModel =
                                        DiscussionIdModel(contentId.userId.userId, Permlink(contentId.permlink))
                                    openPost(discussionIdModel, contentId)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun sharePost(shareUrl: String) {
        requireContext().shareMessage(shareUrl, viewModel.getCurrentUserId())
    }

    private fun openPost(discussionIdModel: DiscussionIdModel, contentId: ContentIdDomain) {
        getDashboardFragment(this)?.navigateToFragment(newInstance(Args(discussionIdModel, contentId)) {}, tag = contentId.permlink)
    }

    private fun showReportPost(contentId: ContentIdDomain) {
        val tag = PostReportDialog::class.java.name
        if (childFragmentManager.findFragmentByTag(tag) == null) {
            val dialog = PostReportDialog.newInstance(PostReportDialog.Args(contentId))
            dialog.onPostReportCompleteCallback = {
                viewModel.sendReport(it)
            }
            dialog.show(childFragmentManager, tag)
        }
    }

    private fun deletePost() =
        ConfirmationDialog.newInstance(R.string.delete_post_confirmation, this@PostPageFragment).show(requireFragmentManager(), MENU)

    private fun openEditPost(contentId: ContentIdDomain) {
        startActivityForResult(EditorPageActivity.getIntent(requireContext(), EditorPageFragment.Args(contentId = contentId)), REQUEST_FOR_RESULT_FROM_EDIT)
    }

    private fun openPostMenuDialog(postMenu: PostMenu) {
        PostPageMenuDialog.show(this, viewModel.isPostSubscriptionModified.value!!, postMenu) {
            when (it) {
                is PostPageMenuDialog.Result.AddFavorite -> viewModel.addToFavorite(it.postMenu.permlink)
                is PostPageMenuDialog.Result.ViewInExplorer -> viewModel.viewInExplorer(it.postMenu)
                is PostPageMenuDialog.Result.RemoveFavorite -> viewModel.removeFromFavorite(it.postMenu.permlink)
                is PostPageMenuDialog.Result.Share -> it.postMenu.shareUrl?.let { viewModel.onShareClicked(it) }
                is PostPageMenuDialog.Result.Edit -> it.postMenu.contentId?.let { viewModel.editPost(it) }
                is PostPageMenuDialog.Result.Delete -> viewModel.deletePost()
                is PostPageMenuDialog.Result.Subscribe -> viewModel.subscribeToCommunity(it.postMenu.communityId)
                is PostPageMenuDialog.Result.Unsubscribe -> viewModel.unsubscribeToCommunity(it.postMenu.communityId)
                is PostPageMenuDialog.Result.Report -> it.postMenu.contentId?.let { viewModel.reportPost(it) }
            }
        }
    }

    private fun showCommentMenu(commentId: ContentIdDomain) {
        CommentsActionsDialog.show(this@PostPageFragment, commentId) {
            when (it) {
                is CommentsActionsDialog.Result.Edit -> viewModel.startEditComment(it.commentId)
                is CommentsActionsDialog.Result.Delete -> viewModel.deleteComment(it.commentId)
            }
        }
    }

    private fun showPostRewardDialog(@StringRes titleResId: Int, @StringRes textResId: Int) =
        PostRewardBottomSheetDialog.show(this@PostPageFragment, titleResId, textResId) {}

    private fun moveToDonate(command: NavigateToDonateCommand) =
        getDashboardFragment(this)?.navigateToFragment(DonateSendPointsFragment.newInstance(command.contentId, command.communityId, command.contentAuthor, command.balance, command.amount))

    private fun showDonationUsersDialogCommand(post:Post) =
        DonationUsersDialog.show(this, post, closeAction = {
            (it as? DonationUsersDialog.Result.ItemSelected)?.user?.let { viewModel.onUserClicked(it.userId) }
        },onUserClickListener = {
            viewModel. onUserClicked(it)
        }, onDonateClickListener = { donateType, contentId, communityId, author ->
            viewModel.onDonateClick(donateType, contentId, communityId, author)
        })


    private fun showSelectRewardCurrencyDialog(currency: RewardCurrency) = SelectRewardCurrencyDialog.show(this, currency) {
        it?.rewardCurrency?.let { currency -> viewModel.updateRewardCurrency(currency) }
    }
}