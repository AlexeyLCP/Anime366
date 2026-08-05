package su.afk.yummy.tv.feature.details.utils

import kotlinx.collections.immutable.toImmutableList
import su.afk.yummy.tv.core.model.anime.AnimeVideo
import su.afk.yummy.tv.feature.details.details.handler.DetailsVideosResult
import su.afk.yummy.tv.feature.details.details.model.VideosUiState

internal fun List<AnimeVideo>.toDetailsVideosResult(
    optimisticSubscriptionKeys: Set<String>,
    optimisticSubscriptionStates: Map<String, Boolean>,
): DetailsVideosResult =
    DetailsVideosResult(
        videos = this,
        videosState = if (isEmpty()) VideosUiState.Empty else VideosUiState.Content(
            this.toImmutableList()
        ),
        subscriptions = toSubscriptionOptions(
            optimisticKeys = optimisticSubscriptionKeys,
            optimisticStates = optimisticSubscriptionStates,
        ),
    )
