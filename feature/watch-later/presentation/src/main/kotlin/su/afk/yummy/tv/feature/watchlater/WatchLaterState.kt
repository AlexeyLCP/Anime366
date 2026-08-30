package su.afk.yummy.tv.feature.watchlater

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import su.afk.yummy.tv.core.mvi.UiEffect
import su.afk.yummy.tv.core.mvi.UiEvent
import su.afk.yummy.tv.core.mvi.UiState
import su.afk.yummy.tv.domain.watchlater.model.WatchLaterItem

class WatchLaterState {
    @Immutable
    data class State(
        val items: ImmutableList<WatchLaterItem> = persistentListOf(),
    ) : UiState

    sealed interface Event : UiEvent {
        data object BackSelected : Event
        data class ItemSelected(val animeId: Int, val episode: String) : Event
        data class RemoveSelected(val animeId: Int, val episode: String) : Event
    }

    /** Экран обходится состоянием — эффектов пока нет. */
    sealed interface Effect : UiEffect
}
