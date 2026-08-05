package su.afk.yummy.tv.feature.details.details.model

import kotlinx.collections.immutable.ImmutableList
import su.afk.yummy.tv.core.model.anime.AnimeRecommendation

sealed interface SimilarUiState {
    data object Loading : SimilarUiState
    data object Empty : SimilarUiState
    data class Error(val message: String?) : SimilarUiState
    data class Content(val items: ImmutableList<AnimeRecommendation>) : SimilarUiState
}
