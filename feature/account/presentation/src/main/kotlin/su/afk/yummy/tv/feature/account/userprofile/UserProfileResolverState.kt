package su.afk.yummy.tv.feature.account.userprofile

import su.afk.yummy.tv.core.mvi.UiEffect
import su.afk.yummy.tv.core.mvi.UiEvent
import su.afk.yummy.tv.core.mvi.UiState

class UserProfileResolverState {
    data class State(val isLoading: Boolean = true, val hasError: Boolean = false) : UiState
    sealed interface Event : UiEvent {
        data object BackSelected : Event
        data object RetrySelected : Event
    }

    sealed interface Effect : UiEffect
}
