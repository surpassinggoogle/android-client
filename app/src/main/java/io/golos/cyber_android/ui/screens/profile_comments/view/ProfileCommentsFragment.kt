package io.golos.cyber_android.ui.screens.profile_comments.view

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.databinding.FragmentProfileCommentsBinding
import io.golos.cyber_android.ui.common.mvvm.FragmentBaseMVVM
import io.golos.cyber_android.ui.common.paginator.Paginator
import io.golos.cyber_android.ui.screens.profile_comments.di.ProfileCommentsFragmentComponent
import io.golos.cyber_android.ui.screens.profile_comments.model.item.ProfileCommentListItem
import io.golos.cyber_android.ui.screens.profile_comments.view.list.ProfileCommentsAdapter
import io.golos.cyber_android.ui.screens.profile_comments.view_model.ProfileCommentsViewModel
import kotlinx.android.synthetic.main.fragment_ftue_search_community.*
import kotlinx.android.synthetic.main.fragment_profile_comments.*
import kotlinx.android.synthetic.main.fragment_profile_comments.btnRetry
import kotlinx.android.synthetic.main.fragment_profile_comments.emptyProgressLoading

class ProfileCommentsFragment : FragmentBaseMVVM<FragmentProfileCommentsBinding, ProfileCommentsViewModel>() {

    override fun provideViewModelType(): Class<ProfileCommentsViewModel> = ProfileCommentsViewModel::class.java

    override fun layoutResId(): Int = R.layout.fragment_profile_comments

    override fun inject() = App.injections.get<ProfileCommentsFragmentComponent>()
        .inject(this)

    override fun releaseInjection() {
        App.injections.release<ProfileCommentsFragmentComponent>()
    }

    override fun linkViewModel(binding: FragmentProfileCommentsBinding, viewModel: ProfileCommentsViewModel) {
        binding.viewModel = viewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupCommentsList()
        observeViewModel()

        btnRetry.setOnClickListener {
            viewModel.onRetryLoadComments()
        }
    }

    private fun setupCommentsList() {
        val communityAdapter = ProfileCommentsAdapter(viewModel)
        val lManager = LinearLayoutManager(requireContext())
        rvComments.layoutManager = lManager
        rvComments.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val visibleItemCount = recyclerView.childCount
                val totalItemCount = lManager.itemCount
                val firstVisibleItem = lManager.findFirstVisibleItemPosition()

                if (totalItemCount - visibleItemCount <= firstVisibleItem + visibleItemCount) {
                    if (lManager.findLastCompletelyVisibleItemPosition() >= totalItemCount - 1) {
                        viewModel.loadMoreComments()
                    }
                }
            }
        })

        rvComments.adapter = communityAdapter
    }

    private fun observeViewModel() {
        viewModel.commentListState.observe(viewLifecycleOwner, Observer { state ->
            val cAdapter = rvComments.adapter as ProfileCommentsAdapter
            when (state) {
                is Paginator.State.Data<*> -> {
                    cAdapter.removeProgress()
                    cAdapter.removeRetry()
                    val items = (state.data as MutableList<ProfileCommentListItem>)
                    cAdapter.update(items)
                    btnRetry.visibility = View.INVISIBLE
                    emptyProgressLoading.visibility = View.INVISIBLE
                }
                is Paginator.State.FullData<*> -> {
                    cAdapter.removeProgress()
                    cAdapter.removeRetry()
                    val items = (state.data as MutableList<ProfileCommentListItem>)
                    cAdapter.update(items)
                    rvComments.scrollToPosition(cAdapter.itemCount - 1)
                    btnRetry.visibility = View.INVISIBLE
                    emptyProgressLoading.visibility = View.INVISIBLE
                }
                is Paginator.State.PageError<*> -> {
                    cAdapter.removeProgress()
                    cAdapter.addRetry()
                    rvComments.scrollToPosition(cAdapter.itemCount - 1)
                }
                is Paginator.State.NewPageProgress<*> -> {
                    cAdapter.removeRetry()
                    cAdapter.addProgress()
                    rvComments.scrollToPosition(cAdapter.itemCount - 1)
                }
                is Paginator.State.EmptyProgress -> {
                    cAdapter.removeProgress()
                    cAdapter.removeRetry()
                    btnRetry.visibility = View.INVISIBLE
                    emptyProgressLoading.visibility = View.VISIBLE
                }
                is Paginator.State.Empty -> {
                    cAdapter.update(mutableListOf())
                    cAdapter.removeProgress()
                    cAdapter.removeRetry()
                    btnRetry.visibility = View.INVISIBLE
                    emptyProgressLoading.visibility = View.INVISIBLE
                }
                is Paginator.State.EmptyError -> {
                    cAdapter.removeProgress()
                    cAdapter.removeRetry()
                    btnRetry.visibility = View.VISIBLE
                    emptyProgressLoading.visibility = View.INVISIBLE
                }
            }
        })
    }

    companion object {
        fun newInstance() = ProfileCommentsFragment()
    }
}