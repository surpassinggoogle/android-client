package io.golos.data.repositories.users

import io.golos.commun4j.Commun4j
import io.golos.commun4j.model.BandWidthRequest
import io.golos.commun4j.model.ClientAuthRequest
import io.golos.commun4j.sharedmodel.CyberName
import io.golos.data.api.user.UsersApi
import io.golos.data.mappers.*
import io.golos.data.repositories.network_call.NetworkCallProxy
import io.golos.domain.DispatchersProvider
import io.golos.domain.GlobalConstants
import io.golos.domain.KeyValueStorageFacade
import io.golos.domain.UserKeyStore
import io.golos.domain.dto.*
import io.golos.domain.repositories.CurrentUserRepository
import io.golos.domain.repositories.UsersRepository
import io.golos.domain.repositories.exceptions.ApiResponseErrorException
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import javax.inject.Inject

class UsersRepositoryImpl
@Inject constructor(
    private val callProxy: NetworkCallProxy,
    private val usersApi: UsersApi,
    private val dispatchersProvider: DispatchersProvider,
    private val commun4j: Commun4j,
    private val currentUserRepository: CurrentUserRepository,
    private val userKeyStore: UserKeyStore,
    private val keyValueStorageFacade: KeyValueStorageFacade
) : UsersRepository {

    override suspend fun clearCurrentUserData() {
        keyValueStorageFacade.removeFtueState()
    }

    override suspend fun isNeedShowFtueBoard(): Boolean {
        val ftueBoardStage = getFtueBoardStage()
        return (ftueBoardStage == FtueBoardStageDomain.NEED_SHOW ||
            ftueBoardStage == FtueBoardStageDomain.SEARCH_COMMUNITIES ||
            ftueBoardStage == FtueBoardStageDomain.FINISH
        )
    }

    override suspend fun setFtueBoardStage(stage: FtueBoardStageDomain) {
        keyValueStorageFacade.saveFtueBoardStage(stage.mapToFtueBoardStageEntity())
    }

    override suspend fun getFtueBoardStage(): FtueBoardStageDomain {
        return keyValueStorageFacade.getFtueBoardStage().mapToFtueBoardStageDomain()
    }


    override suspend fun getFollowers(query: String?, offset: Int, pageSizeLimit: Int): List<FollowerDomain> {
        return withContext(dispatchersProvider.ioDispatcher) {
            usersApi.getFollowers(query, offset, pageSizeLimit)
        }
    }

    override suspend fun subscribeToFollower(userId: UserIdDomain) {
        callProxy.callBC { commun4j.pinUser(
            pinning = CyberName(userId.userId),
            pinner = CyberName(currentUserRepository.userId.userId),
            bandWidthRequest = BandWidthRequest.bandWidthFromComn,
            clientAuthRequest = ClientAuthRequest.empty,
            key = userKeyStore.getKey(UserKeyType.ACTIVE)
        )}
    }

    override suspend fun unsubscribeToFollower(userId: UserIdDomain) {
        callProxy.callBC {
            commun4j.unpinUser(
                unpinning = CyberName(userId.userId),
                pinner = CyberName(currentUserRepository.userId.userId),
                bandWidthRequest = BandWidthRequest.bandWidthFromComn,
                clientAuthRequest = ClientAuthRequest.empty,
                key = userKeyStore.getKey(UserKeyType.ACTIVE)
        )}
    }

    override suspend fun getUserProfile(userId: UserIdDomain): UserProfileDomain =
        try {
            callProxy.call { commun4j.getUserProfile(CyberName(userId.userId), null) }.mapToUserProfileDomain()
        } catch (ex: ApiResponseErrorException) {
            if(ex.errorInfo.code == 404L) {
                callProxy.call { commun4j.getUserProfile(null, userId.userId) }.mapToUserProfileDomain()
            } else {
                throw  ex
            }
        }

    override suspend fun getUserProfile(userName: String): UserProfileDomain =
        try {
            callProxy.call { commun4j.getUserProfile(null, userName) }.mapToUserProfileDomain()
        } catch (ex: ApiResponseErrorException) {
            if(ex.errorInfo.code == 404L) {
                callProxy.call { commun4j.getUserProfile(CyberName(userName), null) }.mapToUserProfileDomain()
            } else {
                throw  ex
            }
        }

    override suspend fun getUserId(userNameOrId: String): UserIdDomain =
        if(userNameOrId.startsWith(GlobalConstants.USER_ID_PREFIX)) {
            UserIdDomain(userNameOrId)
        } else {
            getUserProfile(userNameOrId).userId
        }

    override suspend fun getUserFollowers(userId: UserIdDomain, offset: Int, pageSizeLimit: Int): List<FollowingUserDomain> {
        return callProxy.call {
            commun4j.getSubscribers(
                CyberName(userId.userId),
                null,
                pageSizeLimit,
                offset
            )
        }.items.map { it.mapToFollowingUserDomain() }
    }

    override suspend fun getUserFollowing(userId: UserIdDomain, offset: Int, pageSizeLimit: Int): List<FollowingUserDomain> =
        callProxy.call { commun4j.getUserSubscriptions(CyberName(userId.userId), pageSizeLimit, offset) }.items.map { it.mapToFollowingUserDomain() }

    /**
     * Update cover of current user profile
     * @return url of a cover
     */
    override suspend fun updateCover(coverFile: File): String =
        callProxy.callBC { commun4j.uploadImage(coverFile) }
            .also { url -> updateCurrentUserMetadata { it.copy(coverUrl = url) } }

    /**
     * Update avatar of current user profile
     * @return url of an avatar
     */
    override suspend fun updateAvatar(avatarFile: File): String {
        Timber.tag("NET_SOCKET").d("UsersRepositoryImpl::updateAvatar(${avatarFile.name})")
        return callProxy.callBC {
            Timber.tag("NET_SOCKET").d("upload")
            commun4j.uploadImage(avatarFile)
        }
        .also { url ->
            Timber.tag("NET_SOCKET").d("updateCurrentUserMetadata($url)")
            updateCurrentUserMetadata { it.copy(avatarUrl = url) }
        }
        .also {
            Timber.tag("NET_SOCKET").d("sendUserAvatarChanges")
            currentUserRepository.userAvatarUrl = it
            currentUserRepository.sendUserAvatarChanges()
        }
    }

    /**
     * Clear cover of current user profile
     */
    override suspend fun clearCover() {
        updateCurrentUserMetadata { it.copy(coverUrl = "") }
    }

    /**
     * Clear avatar of current user profile
     */
    override suspend fun clearAvatar() {
        updateCurrentUserMetadata { it.copy(avatarUrl = "") }
            .also {
                currentUserRepository.userAvatarUrl = ""
                currentUserRepository.sendUserAvatarChanges()
            }
    }

    /**
     * Update user's bio
     */
    override suspend fun updateBio(bio: String) {
        updateCurrentUserMetadata { it.copy(biography = bio) }
    }

    /**
     * Clear bio of current user profile
     */
    override suspend fun clearBio() {
        updateCurrentUserMetadata { it.copy(biography = "") }
    }

    override suspend fun getUsersInBlackList(offset: Int, pageSize: Int, userId: UserIdDomain): List<UserDomain> =
        callProxy.call { commun4j.getBlacklistedUsers(CyberName(userId.userId)) }.items.map { it.mapToUserDomain() }

    override suspend fun moveUserToBlackList(userId: UserIdDomain) {
        callProxy.callBC {
            commun4j.block(
                blocking = CyberName(userId.userId),
                blocker = CyberName(currentUserRepository.userId.userId),
                bandWidthRequest = BandWidthRequest.bandWidthFromComn,
                clientAuthRequest = ClientAuthRequest.empty,
                key = userKeyStore.getKey(UserKeyType.ACTIVE))
        }
    }

    override suspend fun moveUserFromBlackList(userId: UserIdDomain) {
        callProxy.callBC {
            commun4j.unBlock(
                blocking = CyberName(userId.userId),
                blocker = CyberName(currentUserRepository.userId.userId),
                bandWidthRequest = BandWidthRequest.bandWidthFromComn,
                clientAuthRequest = ClientAuthRequest.empty,
                key = userKeyStore.getKey(UserKeyType.ACTIVE))
        }
    }

    private suspend fun updateCurrentUserMetadata(requestAction: (UserMetadataRequest) -> UserMetadataRequest) =
        requestAction(
            UserMetadataRequest(
                avatarUrl = null,
                coverUrl = null,
                biography = null,
                facebook = null,
                telegram = null,
                whatsapp = null,
                wechat = null,
                bandWidthRequest = BandWidthRequest.bandWidthFromComn,
                clientAuthRequest = ClientAuthRequest.empty,
                user = CyberName(currentUserRepository.authState!!.user.userId),
                key = userKeyStore.getKey(UserKeyType.ACTIVE))
        )
            .let {
                callProxy.callBC {
                    commun4j.updateUserMetadata(
                        avatarUrl = it.avatarUrl,
                        coverUrl = it.coverUrl,
                        biography = it.biography,
                        facebook = it.facebook,
                        telegram = it.telegram,
                        whatsapp = it.whatsapp,
                        wechat = it.wechat,
                        bandWidthRequest = it.bandWidthRequest,
                        clientAuthRequest = it.clientAuthRequest,
                        user = it.user,
                        key = it.key,
                        first_name = null,
                        last_name = null,
                        country = null,
                        city = null,
                        birth_date = null,
                        instagram = null,
                        linkedin = null,
                        twitter = null,
                        github = null,
                        website_url = null
                    )
                }
            }
}