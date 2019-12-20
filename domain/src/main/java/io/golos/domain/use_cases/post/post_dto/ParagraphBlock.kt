package io.golos.domain.use_cases.post.post_dto

data class ParagraphBlock(
    val id: String?,
    val content: List<ParagraphItemBlock>
) : ParagraphItemBlock