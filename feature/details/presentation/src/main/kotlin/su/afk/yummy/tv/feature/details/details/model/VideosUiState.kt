package su.afk.yummy.tv.feature.details.details.model

import kotlinx.collections.immutable.ImmutableList
import su.afk.yummy.tv.core.model.anime.AnimeVideo

sealed interface VideosUiState {
    data object NotLoaded : VideosUiState
    data object Loading : VideosUiState
    data object Empty : VideosUiState
    data class Error(val message: String?) : VideosUiState
    data class Content(val videos: ImmutableList<AnimeVideo>) : VideosUiState
}
