package io.golos.cyber_android.ui.common.base.adapter.base_items

import android.content.Context
import android.view.View
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.common.base.adapter.BaseRecyclerItem
import kotlinx.android.synthetic.main.item_progress_error.view.*

class ProgressItem: BaseRecyclerItem() {

    override fun getLayoutId(): Int = R.layout.item_progress_error

    override fun initView(context: Context, view: View) {
        super.initView(context, view)
        view.btnPageLoadingRetry.visibility = View.INVISIBLE
    }
}