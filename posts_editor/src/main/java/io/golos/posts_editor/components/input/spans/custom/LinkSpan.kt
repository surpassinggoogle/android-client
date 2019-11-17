package io.golos.posts_editor.components.input.spans.custom

import androidx.annotation.ColorInt
import io.golos.domain.use_cases.post.editor_output.LinkInfo

class LinkSpan(
    data: LinkInfo,
    @ColorInt textColor: Int
) : SpecialSpansBase<LinkInfo>(data, textColor) {

    private val typeId = 2525358

    override fun getSpanTypeId(): Int = typeId
}