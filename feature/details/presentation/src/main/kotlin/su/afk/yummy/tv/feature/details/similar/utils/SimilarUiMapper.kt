package su.afk.yummy.tv.feature.details.similar.utils

import kotlinx.collections.immutable.toImmutableList
import su.afk.yummy.tv.core.model.anime.AnimeRecommendation
import su.afk.yummy.tv.feature.details.details.model.SimilarUiState

internal fun SimilarUiState.updateItem(item: AnimeRecommendation): SimilarUiState =
    if (this is SimilarUiState.Content) {
        copy(items = items.map { current ->
            if (current.animeId == item.animeId) item else current
        }.toImmutableList())
    } else {
        this
    }
