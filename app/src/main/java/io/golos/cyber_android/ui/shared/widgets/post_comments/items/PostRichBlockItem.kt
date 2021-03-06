package io.golos.cyber_android.ui.shared.widgets.post_comments.items

import android.content.Context
import io.golos.cyber_android.ui.shared.base.adapter.RecyclerItem
import io.golos.cyber_android.ui.shared.utils.getScreenSize
import io.golos.cyber_android.ui.shared.widgets.post_comments.RichWidget
import io.golos.cyber_android.ui.shared.widgets.post_comments.RichWidgetListener
import io.golos.domain.dto.ContentIdDomain
import io.golos.domain.posts_parsing_rendering.post_metadata.post_dto.RichBlock

class PostRichBlockItem(
    val richBlock: RichBlock,
    val contentId: ContentIdDomain,
    widgetListener: RichWidgetListener? = null
) : BaseBlockItem<RichBlock, RichWidgetListener, RichWidget>(
    richBlock,
    widgetListener
) {

    override fun createWidget(
        context: Context
    ): RichWidget = RichWidget(context).apply {
        setContentId(contentId)
        setWidthBlock(context.getScreenSize().x)
    }

    override fun areItemsTheSame(): Int = richBlock.hashCode()

    override fun areContentsSame(item: RecyclerItem): Boolean {
        if (item is PostRichBlockItem) {
            return richBlock == item.richBlock
        }
        return false
    }
}