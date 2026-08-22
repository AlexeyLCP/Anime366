package su.afk.yummy.tv.feature.account.mysubscriptions

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import su.afk.yummy.tv.core.mvi.UiEffect
import su.afk.yummy.tv.core.mvi.UiEvent
import su.afk.yummy.tv.core.mvi.UiState
import su.afk.yummy.tv.domain.account.model.VideoSubscription
import su.afk.yummy.tv.feature.account.account.model.AccountUiError

class MySubscriptionsState {
    data class State(
        val isLoading: Boolean = true,
        val error: AccountUiError? = null,
        val isSignedIn: Boolean = true,
        val subscriptions: ImmutableList<VideoSubscription> = persistentListOf(),
    ) : UiState

    sealed interface Event : UiEvent {
        /** Экран показан — в том числе при возврате из тайтла, где подписку могли снять. */
        data object ScreenShown : Event
        data object BackSelected : Event
        data object RetrySelected : Event
        data class SubscriptionSelected(val animeId: Int) : Event
    }

    sealed interface Effect : UiEffect
}
