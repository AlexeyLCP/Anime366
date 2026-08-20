package su.afk.yummy.tv.feature.pages

import su.afk.yummy.tv.core.mvi.UiEffect
import su.afk.yummy.tv.core.mvi.UiEvent
import su.afk.yummy.tv.core.mvi.UiState
import su.afk.yummy.tv.domain.pages.model.SitePage
import su.afk.yummy.tv.domain.pages.model.SitePageType

class SitePagesState {
    data class State(
        val selectedType: SitePageType? = null,
        val page: SitePage? = null,
        val loading: Boolean = false,
        val usingFallback: Boolean = false,
    ) : UiState

    sealed interface Event : UiEvent {
        data object BackSelected : Event
        data class PageSelected(val type: SitePageType) : Event
        data object RetrySelected : Event
    }

    sealed interface Effect : UiEffect
}
