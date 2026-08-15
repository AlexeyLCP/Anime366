package su.afk.yummy.tv.feature.account.userprofile.handler

import su.afk.yummy.tv.domain.account.model.AnimeCollectionSummary
import su.afk.yummy.tv.domain.account.model.UserFriend
import su.afk.yummy.tv.domain.account.model.UserPostSummary
import su.afk.yummy.tv.domain.account.model.UserReviewSummary
import su.afk.yummy.tv.domain.account.usecase.GetUserCollectionsUseCase
import su.afk.yummy.tv.domain.account.usecase.GetUserFriendsUseCase
import su.afk.yummy.tv.domain.account.usecase.GetUserPostsUseCase
import su.afk.yummy.tv.domain.account.usecase.GetUserReviewsUseCase
import javax.inject.Inject

/** Fetches one page of a profile's paged tabs (collections/posts/reviews/friends). */
internal class UserProfilePagingFetchHandler @Inject constructor(
    private val getUserCollections: GetUserCollectionsUseCase,
    private val getUserPosts: GetUserPostsUseCase,
    private val getUserReviews: GetUserReviewsUseCase,
    private val getUserFriends: GetUserFriendsUseCase,
) {
    suspend fun fetchCollections(
        userId: Int,
        limit: Int,
        offset: Int
    ): List<AnimeCollectionSummary> =
        getUserCollections(userId, limit, offset)

    suspend fun fetchPosts(userId: Int, limit: Int, offset: Int): List<UserPostSummary> =
        getUserPosts(userId, limit, offset)

    suspend fun fetchReviews(userId: Int, limit: Int, offset: Int): List<UserReviewSummary> =
        getUserReviews(userId, limit, offset)

    suspend fun fetchFriends(userId: Int, limit: Int, offset: Int): List<UserFriend> =
        getUserFriends(userId, limit, offset)
}
