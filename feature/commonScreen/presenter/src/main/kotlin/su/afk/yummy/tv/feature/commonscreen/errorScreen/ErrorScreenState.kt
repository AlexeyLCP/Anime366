package su.afk.yummy.tv.feature.commonscreen.errorScreen

import su.afk.yummy.tv.core.model.ErrorItem
import su.afk.yummy.tv.core.mvi.UiEffect
import su.afk.yummy.tv.core.mvi.UiEvent
import su.afk.yummy.tv.core.mvi.UiState

internal class ErrorScreenState {

    data class State(
        val error: ErrorItem? = null,
    ) : UiState

    sealed interface Event : UiEvent {
        data object Retry : Event
        data object Back : Event
    }

    sealed interface Effect : UiEffect
}
