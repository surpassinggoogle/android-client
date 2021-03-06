package io.golos.domain.dto.block

import com.squareup.moshi.Json

data class ListContentBlockEntity(
    @Json(name = "id") val id: Long?,
    @Json(name = "type") val type: String?,
    @Json(name = "titles") val titles:String?,
    @Json(name = "attributes") val attributes: DocumentAttributeEntity,
    @Json(name = "content") val content: List<ContentBlockEntity>)