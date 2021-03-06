//package io.golos.cyber_android.interactors
//
//import androidx.arch.core.executor.testing.InstantTaskExecutorRule
//import io.golos.cyber_android.*
//import io.golos.domain.interactors.action.VoteUseCase
//import io.golos.domain.interactors.feed.CommunityFeedUseCase
//import io.golos.domain.interactors.model.CommunityId
//import io.golos.domain.interactors.model.DiscussionsFeed
//import io.golos.domain.interactors.model.PostModel
//import io.golos.domain.interactors.model.UpdateOption
//import io.golos.domain.requestmodel.QueryResult
//import io.golos.domain.requestmodel.VoteRequestModel
//import junit.framework.Assert.assertTrue
//import junit.framework.Assert.fail
//import kotlinx.coroutines.delay
//import kotlinx.coroutines.runBlocking
//import org.junit.Before
//import org.junit.Rule
//import org.junit.Test
//
///**
// * Created by yuri yurivladdurain@gmail.com on 2019-03-18.
// */
//class CommunityFeedUseCaseTest {
//
//
//    @Rule
//    @JvmField
//    public val rule = InstantTaskExecutorRule()
//
//    lateinit var case: CommunityFeedUseCase
//
//    lateinit var voteCase: VoteUseCase
//
//    @Before
//    fun before() {
//        appCore.initialize()
//        case = CommunityFeedUseCase(
//            CommunityId("gls"),
//            feedRepository,
//            voteRepo,
//            feedEntityToModelMapper,
//            dispatchersProvider
//        )
//        voteCase = VoteUseCase(
//            authStateRepository, voteRepo, dispatchersProvider, voteEntityToPostMapper,
//            voteToEntityMapper
//        )
//    }
//
//    @Test
//    fun testOne() = runBlocking {
//        var postFeed: DiscussionsFeed<PostModel>? = null
//        var lastUpdatedChunk: List<PostModel>? = null
//        case.subscribe()
//        case.unsubscribe()
//        case.subscribe()
//
//        voteCase.subscribe()
//
//        case.getAsLiveData.observeForever {
//            postFeed = it
//        }
//        case.getLastFetchedChunk.observeForever {
//            lastUpdatedChunk = it
//        }
//
//        case.requestFeedUpdate(20, UpdateOption.REFRESH_FROM_BEGINNING)
//
//
//        assertTrue("fail of initial loading of posts", postFeed?.items?.size == 20)
//        assertTrue("last updated chunk update fails", lastUpdatedChunk?.size == 20)
//        assertTrue("fail of initial loading of posts", case.getAsLiveData.value?.items?.size == 20)
//
//
//        case.requestFeedUpdate(20, UpdateOption.REFRESH_FROM_BEGINNING)
//
//        assertTrue("fail of reloading first posts", postFeed?.items?.size == 20)
//        assertTrue("last updated chunk update fails", lastUpdatedChunk?.size == 20)
//
//        case.requestFeedUpdate(20, UpdateOption.FETCH_NEXT_PAGE)
//
//        assertTrue("error fetching second page of posts", postFeed?.items?.size == 40)
//        assertTrue("last updated chunk update fails", lastUpdatedChunk?.size == 20)
//
//        case.requestFeedUpdate(20, UpdateOption.FETCH_NEXT_PAGE)
//
//        assertTrue("error fetching third page of posts", postFeed?.items?.size == 60)
//        assertTrue("last updated chunk update fails", lastUpdatedChunk?.size == 20)
//
//        case.requestFeedUpdate(20, UpdateOption.REFRESH_FROM_BEGINNING)
//        assertTrue("last updated chunk update fails", lastUpdatedChunk?.size == 20)
//
//
//        assertTrue(
//            "error loading fresh posts, after downloading 3 consecutive pages of feed",
//            postFeed?.items?.size == 20
//        )
//
//
//        val firstPost = postFeed!!.items.first()
//
//        voteCase.vote(VoteRequestModel.VoteForPostRequest(1_000, firstPost.contentId))
//
//        var counter = 0
//        case.getAsLiveData.observeForever {
//            counter++
//            val firstPost = it.items.first()
//            println(firstPost.votes)
//            when (counter) {
//                1 -> {
//                    if (!firstPost.votes.hasUpVoteProgress) fail("post voting update was not displayed on model")
//                }
//                2 -> if (firstPost.votes.hasUpVoteProgress) fail("post voting update was halted on model")
//                3 -> if (!firstPost.votes.hasDownVotingProgress) fail("post downVoting update was not displayed on model")
//                4 -> if (firstPost.votes.hasDownVotingProgress) fail("post downVoting was halter")
//            }
//
//        }
//
//
//        while (voteCase.getAsLiveData.value.orEmpty()[firstPost.contentId] !is QueryResult.Success) {
//            delay(100)
//        }
//
//
//        delay(2_000)
//        assertTrue(postFeed!!.items.first().votes.hasUpVote)
//
//
//        voteCase.vote(VoteRequestModel.VoteForPostRequest(-1_000, firstPost.contentId))
//
//        while (voteCase.getAsLiveData.value.orEmpty()[firstPost.contentId] !is QueryResult.Success) {
//            delay(100)
//        }
//
//        delay(2_000)
//
//        assertTrue(postFeed!!.items.first().votes.hasDownVote)
//
//        assertTrue("last updated chunk update fails", lastUpdatedChunk?.size == 20)
//
//        (1..5).forEach {
//
//            val post = postFeed!!.items[it]
//
//
//            voteCase.vote(VoteRequestModel.VoteForPostRequest((Math.random() * 10_000).toShort(), post.contentId))
//
//
//            while (voteCase.getAsLiveData.value.orEmpty()[post.contentId] !is QueryResult.Success) {
//                delay(100)
//            }
//
//            delay(2_000)
//
//            assertTrue(postFeed!!.items[it].votes.hasUpVote)
//
//            voteCase.vote(VoteRequestModel.VoteForPostRequest((Math.random() * -10_000).toShort(), post.contentId))
//
//            while (voteCase.getAsLiveData.value.orEmpty()[post.contentId] !is QueryResult.Success) {
//                delay(100)
//            }
//
//            delay(2_000)
//
//            assertTrue(postFeed!!.items[it].votes.hasDownVote)
//        }
//    }
//}