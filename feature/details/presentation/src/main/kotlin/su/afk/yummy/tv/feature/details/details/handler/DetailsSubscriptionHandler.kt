package su.afk.yummy.tv.feature.details.details.handler

import kotlinx.coroutines.delay
import su.afk.yummy.tv.core.model.anime.AnimeDetails
import su.afk.yummy.tv.core.model.anime.AnimeVideo
import su.afk.yummy.tv.domain.account.usecase.GetAccountSessionUseCase
import su.afk.yummy.tv.domain.account.usecase.GetAnimeSubscriptionStateUseCase
import su.afk.yummy.tv.domain.account.usecase.SetVideoSubscriptionUseCase
import su.afk.yummy.tv.domain.anime.usecase.GetAnimeDetailsUseCase
import su.afk.yummy.tv.domain.anime.usecase.GetAnimeVideosUseCase
import su.afk.yummy.tv.feature.details.details.model.SubscriptionOption
import su.afk.yummy.tv.feature.details.mapper.toSubscriptionOptions
import su.afk.yummy.tv.feature.details.utils.SUBSCRIPTION_REFRESH_DELAY
import javax.inject.Inject
import javax.inject.Singleton

/** Loads and mutates video subscription options shared by details and subscriptions screens. */
@Singleton
internal class DetailsSubscriptionHandler @Inject constructor(
    private val getAccountSession: GetAccountSessionUseCase,
    private val getAnimeDetails: GetAnimeDetailsUseCase,
    private val getAnimeVideos: GetAnimeVideosUseCase,
    private val getAnimeSubscriptionState: GetAnimeSubscriptionStateUseCase,
    private val setVideoSubscription: SetVideoSubscriptionUseCase,
) {
    /**
     * Значения кнопок, пока запрос в полёте. Записи живут только на время мутации: после ответа
     * источником правды снова становится [GetAnimeSubscriptionStateUseCase].
     */
    private var pendingStatesByAnimeId = emptyMap<Int, Map<String, Boolean>>()

    fun pendingSubscriptionStates(animeId: Int): Map<String, Boolean> =
        pendingStatesByAnimeId[animeId].orEmpty()

    suspend fun loadScreenSubscriptionBase(animeId: Int): ScreenSubscriptionBaseResult {
        val session = getAccountSession()
        if (!session.isAuthorized || session.userId <= 0) return ScreenSubscriptionBaseResult.SignedOut

        val details = runCatching { getAnimeDetails(animeId) }.getOrNull()
        return runCatching { getAnimeVideos(animeId) }.fold(
            onSuccess = { videos ->
                val subscriptions = loadSubscriptions(
                    animeId = animeId,
                    details = details,
                    videos = videos,
                    userId = session.userId,
                ).getOrElse { videos.toSubscriptionOptions() }
                ScreenSubscriptionBaseResult.Content(
                    ScreenSubscriptionBase(
                        userId = session.userId,
                        details = details,
                        videos = videos,
                        subscriptions = subscriptions,
                    )
                )
            },
            onFailure = { error -> ScreenSubscriptionBaseResult.Failure(error.message, error) },
        )
    }

    suspend fun loadSubscriptions(
        animeId: Int,
        details: AnimeDetails?,
        videos: List<AnimeVideo>,
        userId: Int,
    ): Result<List<SubscriptionOption>> = runCatching {
        val state = getAnimeSubscriptionState(
            userId = userId,
            animeId = animeId,
            animeUrl = details?.animeUrl,
        )
        videos.toSubscriptionOptions(
            state = state,
            pendingStates = pendingSubscriptionStates(animeId),
        )
    }

    suspend fun commitSubscriptionChange(
        userId: Int,
        animeId: Int,
        option: SubscriptionOption,
        subscribed: Boolean,
    ): Boolean {
        setPendingState(animeId, option.key, subscribed)
        return try {
            val result = runCatching {
                setVideoSubscription(
                    userId = userId,
                    animeId = animeId,
                    playerId = option.playerId,
                    player = option.player,
                    dubbing = option.dubbing,
                    videoId = option.subscriptionVideoId,
                    subscribed = subscribed,
                )
            }
            val changed = result.getOrDefault(false)
            if (changed) delay(SUBSCRIPTION_REFRESH_DELAY)
            changed
        } finally {
            clearPendingState(animeId, option.key)
        }
    }

    private fun setPendingState(animeId: Int, key: String, subscribed: Boolean) {
        val current = pendingStatesByAnimeId[animeId].orEmpty()
        pendingStatesByAnimeId += animeId to (current + (key to subscribed))
    }

    private fun clearPendingState(animeId: Int, key: String) {
        val current = pendingStatesByAnimeId[animeId].orEmpty() - key
        pendingStatesByAnimeId = if (current.isEmpty()) {
            pendingStatesByAnimeId - animeId
        } else {
            pendingStatesByAnimeId + (animeId to current)
        }
    }
}

/** Base subscription screen data loaded before remote subscription state is merged. */
internal data class ScreenSubscriptionBase(
    val userId: Int,
    val details: AnimeDetails?,
    val videos: List<AnimeVideo>,
    val subscriptions: List<SubscriptionOption>,
)

/** Result of loading base subscription screen data. */
internal sealed interface ScreenSubscriptionBaseResult {
    data object SignedOut : ScreenSubscriptionBaseResult
    data class Content(val base: ScreenSubscriptionBase) : ScreenSubscriptionBaseResult
    data class Failure(val message: String?, val error: Throwable) : ScreenSubscriptionBaseResult
}
