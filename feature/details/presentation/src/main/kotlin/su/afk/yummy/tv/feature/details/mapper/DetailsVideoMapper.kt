package su.afk.yummy.tv.feature.details.mapper

import kotlinx.collections.immutable.toImmutableList
import su.afk.yummy.tv.core.model.anime.AnimeVideo
import su.afk.yummy.tv.domain.account.model.AnimeSubscriptionState
import su.afk.yummy.tv.feature.details.details.handler.DetailsVideosResult
import su.afk.yummy.tv.feature.details.details.model.SubscriptionOption
import su.afk.yummy.tv.feature.details.details.model.VideosUiState

internal fun List<AnimeVideo>.toDetailsVideosResult(
    knownSubscriptions: List<SubscriptionOption>,
    pendingSubscriptionStates: Map<String, Boolean>,
): DetailsVideosResult =
    DetailsVideosResult(
        videos = this,
        videosState = if (isEmpty()) VideosUiState.Empty else VideosUiState.Content(
            this.toImmutableList()
        ),
        subscriptions = toSubscriptionOptions(
            state = knownSubscriptions.toSubscriptionState(),
            pendingStates = pendingSubscriptionStates,
        ),
    )

/**
 * Переносит уже известное состояние подписок на перечитанный список видео: список серий обновляется
 * чаще, чем подписки, и повторно ходить за ними на каждый рефреш видео не нужно.
 */
private fun List<SubscriptionOption>.toSubscriptionState(): AnimeSubscriptionState {
    val subscribed = filter { it.isSubscribed }
    return AnimeSubscriptionState(
        subscribedKeys = subscribed.map { it.key }.toSet(),
        videoIdsByKey = subscribed.associate { it.key to it.subscriptionVideoId },
    )
}
