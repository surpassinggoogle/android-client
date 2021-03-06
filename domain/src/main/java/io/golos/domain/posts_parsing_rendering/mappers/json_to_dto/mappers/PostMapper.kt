package io.golos.domain.posts_parsing_rendering.mappers.json_to_dto.mappers

import io.golos.domain.posts_parsing_rendering.Attribute
import io.golos.domain.posts_parsing_rendering.BlockType
import io.golos.domain.posts_parsing_rendering.CommonType
import io.golos.domain.posts_parsing_rendering.PostGlobalConstants
import io.golos.domain.posts_parsing_rendering.mappers.json_to_dto.IncompatibleVersionsException
import io.golos.domain.posts_parsing_rendering.post_metadata.post_dto.AttachmentsBlock
import io.golos.domain.posts_parsing_rendering.post_metadata.post_dto.Block
import io.golos.domain.posts_parsing_rendering.post_metadata.post_dto.ContentBlock
import org.json.JSONObject

class PostMapper(mappersFactory: MappersFactory): MapperBase<ContentBlock>(mappersFactory) {
    override fun map(source: JSONObject): ContentBlock {
        val jsonAttributes = source.getAttributes() ?: throw IllegalArgumentException("Post attributes can't be empty, POST")

        val metadata = PostMetadataMapper().map(source)

        if(metadata.version.major > PostGlobalConstants.postFormatVersion.major) {
            throw IncompatibleVersionsException()
        }

        val title = jsonAttributes.tryString(Attribute.TITLE)

        val jsonContent = source.getContentAsArray()

        val commonId = source.tryLong(CommonType.ID)!!
        val commonType = source.tryString(CommonType.TYPE)

        val content = mutableListOf<Block>()
        var attachments: AttachmentsBlock? = null

        for(i in 0 until jsonContent.length()) {
            jsonContent.getJSONObject(i)
                .also { block ->
                    when(val type = block.getType()) {
                        BlockType.PARAGRAPH -> {
                            content.add(mappersFactory.getMapper<ParagraphMapper>(
                                ParagraphMapper::class).map(block))
                        }

                        BlockType.IMAGE -> {
                            content.add(mappersFactory.getMapper<ImageMapper>(
                                ImageMapper::class).map(block))
                        }

                        BlockType.VIDEO -> {
                            content.add(mappersFactory.getMapper<VideoMapper>(
                                VideoMapper::class).map(block))
                        }

                        BlockType.WEBSITE -> {
                            content.add(mappersFactory.getMapper<WebsiteMapper>(
                                WebsiteMapper::class).map(block))
                        }

                        BlockType.ATTACHMENTS -> {
                            attachments = mappersFactory.getMapper<AttachmentsMapper>(
                                AttachmentsMapper::class).map(block)
                        }

                        BlockType.RICH -> {
                            content.add(
                                mappersFactory.getMapper<RichMapper>(RichMapper::class).map(block)
                            )
                        }

                        BlockType.EMBED -> {
                            content.add(
                                mappersFactory.getMapper<EmbedMapper>(EmbedMapper::class).map(block)
                            )
                        }

                        else -> throw UnsupportedOperationException("This type ob block is not supported here: $type")
                    }
                }

        }

        return ContentBlock(
            commonId,
            commonType,
            metadata,
            title,
            content,
            attachments
        )
    }
}