package su.afk.yummy.tv.feature.details.mapper

import kotlinx.collections.immutable.toImmutableList
import su.afk.yummy.tv.core.model.anime.AnimeVideo
import su.afk.yummy.tv.feature.details.details.handler.DetailsVideosResult
import su.afk.yummy.tv.feature.details.details.model.VideosUiState

internal fun List<AnimeVideo>.toDetailsVideosResult(
    pendingSubscriptionStates: Map<String, Boolean> = emptyMap(),
): DetailsVideosResult =
    DetailsVideosResult(
        videos = this,
        videosState = if (isEmpty()) VideosUiState.Empty else VideosUiState.Content(
            this.toImmutableList()
        ),
        subscriptions = toSubscriptionOptions(pendingStates = pendingSubscriptionStates),
    )
