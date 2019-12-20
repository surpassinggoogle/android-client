package io.golos.cyber_android.ui.screens.my_feed.view.list

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import io.golos.cyber_android.ui.common.base.adapter.RecyclerAdapter
import io.golos.cyber_android.ui.common.base.adapter.RecyclerItem
import io.golos.cyber_android.ui.common.base.adapter.base_items.ErrorItem
import io.golos.cyber_android.ui.common.base.adapter.base_items.ProgressItem
import io.golos.cyber_android.ui.dto.Post
import io.golos.cyber_android.ui.dto.User
import io.golos.cyber_android.ui.screens.my_feed.view.items.CreatePostItem
import io.golos.cyber_android.ui.common.widgets.post_comments.items.PostItem
import io.golos.cyber_android.ui.screens.my_feed.view_model.MyFeedListListener

open class MyFeedAdapter(
    private val eventsProcessor: MyFeedListListener,
    private val type: PostItem.Type
) : RecyclerAdapter() {

    private val rvViewPool = RecyclerView.RecycledViewPool()

    var onPageRetryLoadingCallback: (() -> Unit)? = null

    fun updateMyFeedPosts(posts: List<Post>) {
        val postsItems = posts.map {
            val postItem = PostItem(it, type, eventsProcessor)
            postItem.setRecycledViewPool(rvViewPool)
            postItem
        }
        val adapterItemsList: ArrayList<RecyclerItem> = ArrayList(postsItems)
        val createPostItem = items.find { it is CreatePostItem }
        if(createPostItem != null){
            adapterItemsList.add(0, createPostItem)
        }
        val progressItem = items.find { it is ProgressItem }
        if(progressItem != null){
            adapterItemsList.add(progressItem)
        }
        val errorItem = items.find { it is ErrorItem }
        if(errorItem != null){
            adapterItemsList.add(errorItem)
        }
        updateAdapter(adapterItemsList)
    }

    fun updateUser(user: User) {
        val adapterItemsList = ArrayList(items)
        val createPostItem = adapterItemsList.find { it is CreatePostItem }
        if (createPostItem == null) {
            adapterItemsList.add(0, CreatePostItem(user, eventsProcessor))
        } else {
            adapterItemsList[0] = CreatePostItem(user, eventsProcessor)
        }
        updateAdapter(adapterItemsList)
    }

    fun showLoadingNextPageProgress() {
        val adapterItemsList = ArrayList(items)
        val hasProgressItem = adapterItemsList.find { it is ProgressItem } != null
        if (!hasProgressItem) {
            adapterItemsList.add(ProgressItem())
            updateAdapter(adapterItemsList)
        }
    }

    fun hideLoadingNextPageProgress() {
        val adapterItemsList = ArrayList(items)
        val progressItem = adapterItemsList.find { it is ProgressItem }
        adapterItemsList.remove(progressItem)
        updateAdapter(adapterItemsList)
    }

    fun showLoadingNextPageError() {
        val adapterItemsList = ArrayList(items)
        val hasErrorItem = adapterItemsList.find { it is ErrorItem } != null
        if (!hasErrorItem) {
            adapterItemsList.add(
                ErrorItem(
                    View.OnClickListener {
                        onPageRetryLoadingCallback?.invoke()
                    }
                )
            )
            updateAdapter(adapterItemsList)
        }
    }

    fun hideLoadingNextPageError() {
        val adapterItemsList = ArrayList(items)
        val errorItem = adapterItemsList.find { it is ErrorItem }
        adapterItemsList.remove(errorItem)
        updateAdapter(adapterItemsList)
    }

    fun clearAllPosts() {
        val deletedItems = ArrayList(items)
        items.removeAll(deletedItems.filter { it !is CreatePostItem })
        updateAdapter(items)
    }
}