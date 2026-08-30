package su.afk.yummy.tv.data.account.network

import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import kotlinx.coroutines.flow.first
import su.afk.yummy.tv.core.analytics.api.AnalyticsTracker
import su.afk.yummy.tv.core.network.BuildConfig
import su.afk.yummy.tv.core.network.anime365.Anime365AccessTokenDto
import su.afk.yummy.tv.core.network.anime365.Anime365ErrorDto
import su.afk.yummy.tv.core.network.anime365.Anime365UserItemDto
import su.afk.yummy.tv.core.network.yani.YANI_BASE_URL
import su.afk.yummy.tv.core.network.yani.YaniApiJson
import su.afk.yummy.tv.core.network.yani.YaniHttpClientProvider
import su.afk.yummy.tv.core.preferences.auth.YaniAuthPreferences
import su.afk.yummy.tv.data.account.dto.YaniAnimeListStatDto
import su.afk.yummy.tv.data.account.dto.YaniAnimeListStateDto
import su.afk.yummy.tv.data.account.dto.YaniAnimeUserRatingDto
import su.afk.yummy.tv.data.account.dto.YaniCollectionSummaryDto
import su.afk.yummy.tv.data.account.dto.YaniPostVideoItemDto
import su.afk.yummy.tv.data.account.dto.YaniProfileDto
import su.afk.yummy.tv.data.account.dto.YaniProfileUpdateBodyDto
import su.afk.yummy.tv.data.account.dto.YaniRatingBucketDto
import su.afk.yummy.tv.data.account.dto.YaniNotificationCountDto
import su.afk.yummy.tv.data.account.dto.YaniNotificationDto
import su.afk.yummy.tv.data.account.dto.YaniRegistrationBodyDto
import su.afk.yummy.tv.data.account.dto.YaniUserAnimeDto
import su.afk.yummy.tv.data.account.dto.YaniUserAnimeTypeStatDto
import su.afk.yummy.tv.data.account.dto.YaniUserFriendDto
import su.afk.yummy.tv.data.account.dto.YaniUserGenreStatDto
import su.afk.yummy.tv.data.account.dto.YaniUserListWatchStatDto
import su.afk.yummy.tv.data.account.dto.YaniUserPostDto
import su.afk.yummy.tv.data.account.dto.YaniUserProfileDto
import su.afk.yummy.tv.data.account.dto.YaniUserProfileResponseDto
import su.afk.yummy.tv.data.account.dto.YaniUserRatingStatDto
import su.afk.yummy.tv.data.account.dto.YaniUserReviewDto
import su.afk.yummy.tv.data.account.dto.YaniVideoSubscriptionDto
import su.afk.yummy.tv.domain.account.model.LinkedAccountProvider

class YaniCaptchaRequiredException : RuntimeException("Captcha required")
class YaniAccountException(message: String, val code: Int? = null) : RuntimeException(message)

class YaniAccountApi(
    private val clientProvider: YaniHttpClientProvider,
    private val analyticsTracker: AnalyticsTracker,
    private val yaniAuthPreferences: YaniAuthPreferences,
) {

    suspend fun unlinkAccount(provider: LinkedAccountProvider): Boolean = false

    suspend fun login(login: String, password: String, captchaResponse: String? = null): String {
        val appId = BuildConfig.ANIME365_APP_ID
        if (appId.isBlank()) {
            throw YaniAccountException(
                "Создайте API-клиент на https://anime-365.ru/api-clients и укажите anime365.appId",
            )
        }
        analyticsTracker.log(TAG) { "GET /login" }
        val response = clientProvider.get().get("$YANI_BASE_URL/login") {
            parameter("app", appId)
            parameter("email", login)
            parameter("password", password)
        }
        if (!response.status.isSuccess()) {
            val error = runCatching {
                YaniApiJson.decodeFromString(Anime365ErrorDto.serializer(), response.bodyAsText())
            }.getOrNull()?.error
            throw YaniAccountException(error?.message?.ifBlank { "Could not sign in" } ?: "Could not sign in", error?.code)
        }
        val token = response.body<Anime365AccessTokenDto>().data?.accessToken.orEmpty()
        if (token.isBlank()) throw YaniAccountException("Could not sign in")
        return token
    }

    suspend fun register(body: YaniRegistrationBodyDto) {
        throw YaniAccountException("Регистрация — на https://anime-365.ru")
    }

    suspend fun verifyRegistration(hash: String): String {
        throw YaniAccountException("Регистрация — на https://anime-365.ru")
    }

    suspend fun refreshToken(): String {
        val token = yaniAuthPreferences.refreshToken.first()
        if (token.isBlank()) return ""
        val profile = getProfile(token)
        return if (profile.id > 0) token else ""
    }

    suspend fun getProfile(token: String? = null): YaniProfileDto {
        val response = clientProvider.get().get("$YANI_BASE_URL/me") {
            token?.trim()?.takeIf { it.isNotBlank() }?.let { parameter("access_token", it) }
        }.body<Anime365UserItemDto>().data
        if (response == null || !response.isLogined) return YaniProfileDto()
        return YaniProfileDto(id = response.id, nickname = response.name)
    }

    suspend fun updateOnline(deviceHash: String) = Unit

    suspend fun getUserProfile(userId: Int): YaniUserProfileResponseDto = YaniUserProfileResponseDto()

    suspend fun searchUsers(query: String, limit: Int, offset: Int): List<YaniUserProfileDto> = emptyList()

    suspend fun getUserProfileByNickname(nickname: String): YaniUserProfileResponseDto =
        YaniUserProfileResponseDto()

    suspend fun getFriendshipStatus(userId: Int, friendId: Int): String? = null

    suspend fun addFriend(userId: Int, friendId: Int) = Unit

    suspend fun removeFriend(userId: Int, friendId: Int) = Unit

    suspend fun logout() = Unit

    suspend fun updateProfile(body: YaniProfileUpdateBodyDto) = Unit

    suspend fun uploadAvatar(userId: Int, bytes: ByteArray) = Unit

    suspend fun deleteAvatar(userId: Int) = Unit

    suspend fun uploadBanner(userId: Int, bytes: ByteArray) = Unit

    suspend fun deleteBanner(userId: Int) = Unit

    suspend fun changePassword(oldPassword: String, newPassword: String): String {
        throw YaniAccountException("Смена пароля — на сайте Anime365")
    }

    suspend fun requestPasswordReset(email: String, captchaResponse: String?) {
        throw YaniAccountException("Сброс пароля — на сайте Anime365")
    }

    suspend fun getUserList(userId: Int, listId: Int): List<YaniUserAnimeDto> = emptyList()

    suspend fun getAllUserLists(userId: Int): List<YaniUserAnimeDto> = emptyList()

    suspend fun getUserFriends(userId: Int, limit: Int, offset: Int): List<YaniUserFriendDto> = emptyList()

    suspend fun getUserReviews(userId: Int, limit: Int, offset: Int): List<YaniUserReviewDto> = emptyList()

    suspend fun getUserPosts(userId: Int, limit: Int, offset: Int): List<YaniUserPostDto> = emptyList()

    suspend fun getUserCollections(userId: Int, limit: Int, offset: Int): List<YaniCollectionSummaryDto> =
        emptyList()

    suspend fun getAnimeListState(animeId: Int): YaniAnimeListStateDto = YaniAnimeListStateDto()

    suspend fun setAnimeList(animeId: Int, listId: Int) = Unit

    suspend fun removeAnimeList(animeId: Int) = Unit

    suspend fun setFavorite(animeId: Int) = Unit

    suspend fun removeFavorite(animeId: Int) = Unit

    suspend fun markWatched(
        videoId: Int,
        timeSeconds: Int,
        durationSeconds: Int,
        times: List<Int>,
    ): Boolean = true

    suspend fun syncWatched(videos: List<YaniPostVideoItemDto>): Boolean = true

    suspend fun removeWatched(videoIds: List<Int>): Boolean = true

    suspend fun getRatingBuckets(animeId: Int): List<YaniRatingBucketDto> = emptyList()

    suspend fun getAnimeListStats(animeId: Int): List<YaniAnimeListStatDto> = emptyList()

    suspend fun getUserRating(animeId: Int): YaniAnimeUserRatingDto = YaniAnimeUserRatingDto()

    suspend fun setRating(animeId: Int, rating: Int) = Unit

    suspend fun deleteRating(animeId: Int) = Unit

    suspend fun getAnimeCollections(animeId: Int, limit: Int, offset: Int): List<YaniCollectionSummaryDto> =
        emptyList()

    suspend fun setSubscribed(videoId: Int): Boolean = false

    suspend fun removeSubscribed(videoId: Int): Boolean = false

    suspend fun getSubscriptions(userId: Int): List<YaniVideoSubscriptionDto> = emptyList()

    suspend fun getUserStatsGenres(userId: Int): List<YaniUserGenreStatDto> = emptyList()

    suspend fun getUserStatsRatings(userId: Int): List<YaniUserRatingStatDto> = emptyList()

    suspend fun getUserStatsLists(userId: Int): List<YaniUserListWatchStatDto> = emptyList()

    suspend fun getUserStatsTypes(userId: Int): List<YaniUserAnimeTypeStatDto> = emptyList()

    suspend fun getNotifications(limit: Int, offset: Int): List<YaniNotificationDto> = emptyList()

    suspend fun getNotificationCounts(): List<YaniNotificationCountDto> = emptyList()

    suspend fun getNotificationAnimeId(slug: String): Int? = slug.toIntOrNull()

    suspend fun markNotificationRead(id: Int): Boolean = true

    suspend fun markAllNotificationsRead(): Boolean = true

    suspend fun deleteNotification(id: Int): Boolean = true

    suspend fun deleteAllNotifications(): Boolean = true

    private companion object {
        const val TAG = "YaniAccountApi"
    }
}
