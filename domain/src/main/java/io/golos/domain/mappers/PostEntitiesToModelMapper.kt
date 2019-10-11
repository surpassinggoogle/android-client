package io.golos.domain.mappers

import io.golos.domain.HtmlToSpannableTransformer
import io.golos.domain.commun_entities.CommunityId
import io.golos.domain.dependency_injection.scopes.ApplicationScope
import io.golos.domain.entities.DiscussionRelatedEntities
import io.golos.domain.entities.PostEntity
import io.golos.domain.entities.TagEntity
import io.golos.domain.extensions.asElapsedTime
import io.golos.domain.interactors.model.*
import io.golos.domain.requestmodel.QueryResult
import java.util.*
import javax.inject.Inject
import kotlin.collections.HashMap

interface PostEntitiesToModelMapper : EntityToModelMapper<DiscussionRelatedEntities<PostEntity>, PostModel> {
    fun map(post: PostEntity): PostModel
}

@ApplicationScope
class PostEntitiesToModelMapperImpl
@Inject
constructor(
    private val htmlToSpannableTransformer: HtmlToSpannableTransformer
) : PostEntitiesToModelMapper {

    private val cashedValues = Collections.synchronizedMap(HashMap<DiscussionRelatedEntities<PostEntity>, PostModel>())
    private val cashedSpans = Collections.synchronizedMap(HashMap<String, CharSequence>())

    override suspend fun map(entity: DiscussionRelatedEntities<PostEntity>): PostModel {
        val post = entity.discussionEntity

        val voteEntity = entity.voteStateEntity

        val out = cashedValues.getOrPut(entity) {
            PostModel(
                DiscussionIdModel(post.contentId.userId, post.contentId.permlink),
                DiscussionAuthorModel(
                    post.author.userId,
                    post.author.username,
                    post.author.avatarUrl
                ),
                CommunityModel(
                    CommunityId(post.community.id),
                    post.community.name,
                    post.community.avatarUrl
                ),
                PostContentModel(
                    ContentBodyModel(
                        post.content.body.postBlock
                    ),
                    post.content.tags.map { it.toModel() }
                ),
                DiscussionVotesModel(
                    post.votes.hasUpVote,
                    post.votes.hasDownVote,
                    post.votes.upCount,
                    post.votes.downCount,
                    (voteEntity is QueryResult.Loading && voteEntity.originalQuery.power > 0),
                    (voteEntity is QueryResult.Loading && voteEntity.originalQuery.power < 0),
                    (voteEntity is QueryResult.Loading && voteEntity.originalQuery.power == 0.toShort())
                ),
                DiscussionCommentsCountModel(post.comments.count),
                DiscussionPayoutModel(),
                DiscussionMetadataModel(post.meta.time, post.meta.time.asElapsedTime()),
                DiscussionStatsModel(post.stats.rShares, post.stats.viewsCount)
            )
        }
        return out
    }

    override fun map(post: PostEntity): PostModel =
        PostModel(
            DiscussionIdModel(post.contentId.userId, post.contentId.permlink),
            DiscussionAuthorModel(
                post.author.userId,
                post.author.username,
                post.author.avatarUrl
            ),
            CommunityModel(
                CommunityId(post.community.id),
                post.community.name,
                post.community.avatarUrl
            ),
            PostContentModel(
                ContentBodyModel(
                    post.content.body.postBlock
                ),
                post.content.tags.map { it.toModel() }
            ),
            DiscussionVotesModel(
                post.votes.hasUpVote,
                post.votes.hasDownVote,
                post.votes.upCount,
                post.votes.downCount,
                false,
                false,
                false
            ),
            DiscussionCommentsCountModel(post.comments.count),
            DiscussionPayoutModel(),
            DiscussionMetadataModel(post.meta.time, post.meta.time.asElapsedTime()),
            DiscussionStatsModel(post.stats.rShares, post.stats.viewsCount)
        )

    private fun TagEntity.toModel() = TagModel(this.tag)
}