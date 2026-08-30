package su.afk.yummy.tv.data.reviews.network

import su.afk.yummy.tv.data.reviews.dto.YaniBooleanResponseDto
import su.afk.yummy.tv.data.reviews.dto.YaniReviewResponseDto
import su.afk.yummy.tv.data.reviews.dto.YaniReviewVoteResponseDto
import su.afk.yummy.tv.data.reviews.dto.YaniReviewsFeedResponseDto
import su.afk.yummy.tv.data.reviews.dto.YaniReviewsPageResponseDto
import javax.inject.Inject

class YaniReviewsApi @Inject constructor() {
    suspend fun getReviews(sort: String, limit: Int, offset: Int): YaniReviewsFeedResponseDto =
        YaniReviewsFeedResponseDto()

    suspend fun getAnimeReviews(
        animeId: Int,
        sort: String,
        limit: Int,
        offset: Int,
    ): YaniReviewsPageResponseDto = YaniReviewsPageResponseDto()

    suspend fun getReview(reviewId: Int): YaniReviewResponseDto = YaniReviewResponseDto()

    suspend fun delete(reviewId: Int): YaniBooleanResponseDto = YaniBooleanResponseDto()

    suspend fun vote(reviewId: Int, action: Int): YaniReviewVoteResponseDto = YaniReviewVoteResponseDto()

    suspend fun removeVote(reviewId: Int): YaniReviewVoteResponseDto = YaniReviewVoteResponseDto()
}
