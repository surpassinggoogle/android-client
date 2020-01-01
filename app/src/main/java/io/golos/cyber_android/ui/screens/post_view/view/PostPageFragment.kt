package io.golos.cyber_android.ui.screens.post_view.view

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.ui.screens.post_view.di.PostPageFragmentComponent
import io.golos.cyber_android.databinding.FragmentPostBinding
import io.golos.cyber_android.ui.shared.Tags
import io.golos.cyber_android.ui.shared.ImageViewerActivity
import io.golos.cyber_android.ui.shared.extensions.reduceDragSensitivity
import io.golos.cyber_android.ui.shared.mvvm.FragmentBaseMVVM
import io.golos.cyber_android.ui.shared.mvvm.view_commands.*
import io.golos.cyber_android.ui.dialogs.CommentsActionsDialog
import io.golos.cyber_android.ui.dialogs.ConfirmationDialog
import io.golos.cyber_android.ui.dialogs.PostPageSortingComments
import io.golos.cyber_android.ui.dto.ContentId
import io.golos.cyber_android.ui.screens.post_edit.view.EditorPageActivity
import io.golos.cyber_android.ui.screens.post_page_menu.model.PostMenu
import io.golos.cyber_android.ui.screens.post_page_menu.view.PostPageMenuDialog
import io.golos.cyber_android.ui.screens.post_report.view.PostReportDialog
import io.golos.cyber_android.ui.screens.profile.view.ProfileExternalUserFragment
import io.golos.cyber_android.ui.screens.post_edit.view.EditorPageFragment
import io.golos.cyber_android.ui.screens.post_view.dto.SortingType
import io.golos.cyber_android.ui.screens.post_view.view.list.PostPageAdapter
import io.golos.cyber_android.ui.screens.post_view.view_commands.*
import io.golos.cyber_android.ui.screens.post_view.view_model.PostPageViewModel
import io.golos.cyber_android.ui.shared.utils.openImageView
import io.golos.cyber_android.ui.shared.utils.openLinkView
import io.golos.cyber_android.ui.shared.utils.shareMessage
import io.golos.domain.dto.UserIdDomain
import io.golos.domain.use_cases.model.DiscussionIdModel
import io.golos.domain.use_cases.model.PostModel
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.fragment_post.*

/**
 * Fragment for single [PostModel] presentation
 */
class PostPageFragment : FragmentBaseMVVM<FragmentPostBinding, PostPageViewModel>() {
    @Parcelize
    data class Args(
        val id: DiscussionIdModel,
        val contentId: ContentId? = null,
        val scrollToComments: Boolean = false
    ) : Parcelable

    override fun provideViewModelType(): Class<PostPageViewModel> = PostPageViewModel::class.java

    override fun layoutResId(): Int = R.layout.fragment_post

    override fun inject() =
        App.injections.get<PostPageFragmentComponent>(
            arguments!!.getParcelable<Args>(Tags.ARGS)!!.id,
            arguments!!.getParcelable<Args>(Tags.ARGS)!!.contentId
        ).inject(this)

    override fun linkViewModel(binding: FragmentPostBinding, viewModel: PostPageViewModel) {
        binding.viewModel = viewModel
    }

    override fun releaseInjection() {
        App.injections.release<PostPageFragmentComponent>()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.post.observe(this, Observer {
            (postView.adapter as PostPageAdapter).update(it)
        })

        postView.reduceDragSensitivity()

        postHeader.setOnBackButtonClickListener { activity?.finish() }
        postHeader.setOnMenuButtonClickListener { viewModel.onPostMenuClick() }
        postHeader.setOnUserClickListener { viewModel.onUserInHeaderClick(it) }

        postComment.setOnSendClickListener { viewModel.onSendCommentClick(it) }

        postCommentEdit.setOnCloseClickListener { viewModel.cancelReplyOrEditComment() }
        postCommentEdit.setOnSendClickListener { viewModel.completeReplyOrEditComment(it) }

        // Setup the list
        val adapter = PostPageAdapter(viewModel, viewModel.commentsPageSize)
        adapter.setHasStableIds(true)
        postView.adapter = adapter

        val postListLayoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        postView.layoutManager = postListLayoutManager
        (postView.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
    }

    override fun onResume() {
        super.onResume()
        viewModel.setup()
    }

    override fun processViewCommand(command: ViewCommand) {
        when (command) {
            is NavigateToMainScreenCommand -> activity?.finish()

            is NavigationToParentScreenWithStringResultCommand -> {
                val permlink = command.permlink
                val intent = Intent(Tags.ACTION_DELETE).apply {
                    putExtra(Tags.PERMLINK_EXTRA, permlink)
                }
                activity?.setResult(Activity.RESULT_OK, intent)
                activity?.finish()
            }

            is NavigateToImageViewCommand -> requireContext().openImageView(command.imageUri)

            is NavigateToLinkViewCommand -> requireContext().openLinkView(command.link)

            is NavigateToUserProfileViewCommand -> openUserProfile(command.userId)

            is StartEditPostViewCommand -> moveToEditPost(command.postId)

            is NavigationToEditPostViewCommand -> openEditPost(command.contentId)

            is NavigationToPostMenuViewCommand -> openPostMenuDialog(command.postMenu)

            is ShowCommentsSortingMenuViewCommand -> showCommentsSortingMenu()

            is ClearCommentTextViewCommand -> postComment.clearText()

            is ShowCommentMenuViewCommand -> showCommentMenu(command.commentId)

            is SharePostCommand -> sharePost(command.shareUrl)

            is ReportPostCommand -> showReportPost(command.contentId)

            is DeletePostCommand -> deletePost()

            else -> throw UnsupportedOperationException("This command is not supported")
        }
    }

    private fun openUserProfile(userId: String){
        getDashboardFragment(this)?.showFragment(ProfileExternalUserFragment.newInstance(UserIdDomain(userId)))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            PostPageMenuDialog.REQUEST -> {
                when (resultCode) {
                    PostPageMenuDialog.RESULT_ADD_FAVORITE -> {
                        val postMenu: PostMenu? = data?.extras?.getParcelable(Tags.POST_MENU)
                        postMenu?.let {
                            viewModel.addToFavorite(it.permlink)
                        }
                    }
                    PostPageMenuDialog.RESULT_REMOVE_FAVORITE -> {
                        val postMenu: PostMenu? = data?.extras?.getParcelable(Tags.POST_MENU)
                        postMenu?.let {
                            viewModel.removeFromFavorite(it.permlink)
                        }
                    }
                    PostPageMenuDialog.RESULT_SHARE -> {
                        val postMenu: PostMenu? = data?.extras?.getParcelable(Tags.POST_MENU)
                        val shareUrl = postMenu?.shareUrl
                        shareUrl?.let {
                            viewModel.onShareClicked(it)
                        }
                    }
                    PostPageMenuDialog.RESULT_EDIT -> {
                        val postMenu: PostMenu? = data?.extras?.getParcelable(Tags.POST_MENU)
                        postMenu?.let { menu ->
                            menu.contentId?.let { contentId ->
                                viewModel.editPost(contentId)
                            }
                        }
                    }
                    PostPageMenuDialog.RESULT_DELETE -> {
                        val postMenu: PostMenu? = data?.extras?.getParcelable(Tags.POST_MENU)
                        postMenu?.let {
                            viewModel.deletePost()
                        }
                    }
                    PostPageMenuDialog.RESULT_SUBSCRIBE -> {
                        val postMenu: PostMenu? = data?.extras?.getParcelable(Tags.POST_MENU)
                        val communityId = postMenu?.communityId
                        communityId?.let {
                            viewModel.subscribeToCommunity(it)
                        }
                    }
                    PostPageMenuDialog.RESULT_UNSUBSCRIBE -> {
                        val postMenu: PostMenu? = data?.extras?.getParcelable(Tags.POST_MENU)
                        postMenu?.communityId?.let {
                            viewModel.unsubscribeToCommunity(it)
                        }
                    }
                    PostPageMenuDialog.RESULT_REPORT -> {
                        val postMenu: PostMenu? = data?.extras?.getParcelable(Tags.POST_MENU)
                        postMenu?.let { menu ->
                            menu.contentId?.let { contentId ->
                                viewModel.reportPost(contentId)
                            }
                        }
                    }
                }

            }
            PostPageSortingComments.REQUEST -> {
                when (resultCode) {
                    PostPageSortingComments.RESULT_INTERESTING_FIRST -> viewModel.updateCommentsSorting(SortingType.INTERESTING_FIRST)
                    PostPageSortingComments.RESULT_BY_TIME -> viewModel.updateCommentsSorting(SortingType.BY_TIME)
                }
            }
            CommentsActionsDialog.REQUEST -> {
                when (resultCode) {
                    CommentsActionsDialog.RESULT_EDIT ->
                        viewModel.startEditComment(data!!.getParcelableExtra(CommentsActionsDialog.COMMENT_ID))
                    CommentsActionsDialog.RESULT_DELETE ->
                        viewModel.deleteComment(data!!.getParcelableExtra(CommentsActionsDialog.COMMENT_ID))
                }
            }
            ConfirmationDialog.REQUEST -> {
                if(resultCode == ConfirmationDialog.RESULT_OK) {
                    viewModel.deletePost()
                }
            }
        }
    }

    private fun sharePost(shareUrl: String) {
        requireContext().shareMessage(shareUrl)
    }

    private fun showReportPost(contentId: ContentId) {
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
        ConfirmationDialog.newInstance(R.string.delete_post_confirmation, this@PostPageFragment)
            .show(requireFragmentManager(), "menu")

    private fun moveToUserProfile(userId: String) =
        getDashboardFragment(this)?.showFragment(ProfileExternalUserFragment.newInstance(UserIdDomain(userId)))

    private fun moveToImageView(imageUri: Uri) =
        startActivity(ImageViewerActivity.getIntent(requireContext(), imageUri.toString()))

    private fun moveToLinkView(link: Uri) {
        Intent(Intent.ACTION_VIEW, link)
            .also { intent ->
                if (intent.resolveActivity(requireActivity().packageManager) != null) {
                    startActivity(intent)
                }
            }
    }

    private fun moveToEditPost(postId: ContentId) =
        startActivity(EditorPageActivity.getIntent(requireContext(), EditorPageFragment.Args(postId)))

    private fun openEditPost(contentId: ContentId) {
        startActivity(
            EditorPageActivity.getIntent(
                requireContext(),
                EditorPageFragment.Args(contentId = contentId)
            )
        )
    }

    private fun openPostMenuDialog(postMenu: PostMenu) {
        PostPageMenuDialog.newInstance(postMenu).apply {
            setTargetFragment(this@PostPageFragment, PostPageMenuDialog.REQUEST)
        }.show(requireFragmentManager(), "show")
    }

    private fun showCommentsSortingMenu() {
        PostPageSortingComments.newInstance().apply {
            setTargetFragment(this@PostPageFragment, PostPageSortingComments.REQUEST)
        }.show(requireFragmentManager(), "menu")
    }

    private fun showCommentMenu(commentId: DiscussionIdModel) {
        CommentsActionsDialog.newInstance(commentId).apply {
            setTargetFragment(this@PostPageFragment, CommentsActionsDialog.REQUEST)
        }.show(requireFragmentManager(), "menu")
    }
}