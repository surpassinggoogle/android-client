package io.golos.domain.posts_parsing_rendering.mappers.json_to_dto.mappers

import io.golos.domain.posts_parsing_rendering.Attribute
import io.golos.domain.posts_parsing_rendering.CommonType
import io.golos.domain.posts_parsing_rendering.post_metadata.post_dto.LinkBlock
import org.json.JSONObject

class LinkMapper(mappersFactory: MappersFactory) : MapperBase<LinkBlock>(mappersFactory) {
    override fun map(source: JSONObject): LinkBlock {
        val attributes = if(source.getAttributes() == null) {
            val withoutSlashes = source.getString("attributes").replace("\\\"","")
            source.put("attributes",JSONObject(withoutSlashes))
            source.getAttributes()!!
        }else{
            source.getAttributes()!!
        }

        return LinkBlock(
            source.tryLong(CommonType.ID),
            source.getContentAsString(),
            attributes.getUri(Attribute.URL)
        )
    }
}