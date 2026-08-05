package su.afk.yummy.tv.feature.library

import androidx.compose.runtime.Immutable
import androidx.paging.PagingData
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import su.afk.yummy.tv.core.designsystem.presenter.baseViewModel.UiEffect
import su.afk.yummy.tv.core.designsystem.presenter.baseViewModel.UiEvent
import su.afk.yummy.tv.core.designsystem.presenter.baseViewModel.UiState
import su.afk.yummy.tv.core.preferences.settings.LibraryContinueWatchingCardSize
import su.afk.yummy.tv.domain.home.model.HomeContinueWatchingItem
import su.afk.yummy.tv.domain.library.model.LibraryItem
import su.afk.yummy.tv.domain.library.model.WatchHistoryEntry
import su.afk.yummy.tv.feature.library.model.LibraryRemoveTarget
import su.afk.yummy.tv.feature.library.model.LibraryTab

class LibraryState {
    @Immutable
    data class State(
        val items: ImmutableList<LibraryItem> = persistentListOf(),
        val tabItems: ImmutableMap<LibraryTab, ImmutableList<LibraryItem>> = persistentMapOf(),
        val continueWatching: ImmutableList<HomeContinueWatchingItem> = persistentListOf(),
        val watchHistory: Flow<PagingData<WatchHistoryEntry>> = flowOf(PagingData.empty()),
        val isSignedIn: Boolean = false,
        val isRemoteLoading: Boolean = false,
        val remoteError: String? = null,
        val selectedTab: LibraryTab = LibraryTab.CONTINUE_WATCHING,
        val continueWatchingCardSize: LibraryContinueWatchingCardSize =
            LibraryContinueWatchingCardSize.LARGE,
    ) : UiState

    /** Пользовательские действия на экране библиотеки. */
    sealed interface Event : UiEvent {
        /** Пользователь выбрал аниме с указанным идентификатором. */
        data class AnimeSelected(val animeId: Int) : Event

        /** Пользователь выбрал элемент продолжения просмотра. */
        data class ContinueWatchingSelected(val entry: HomeContinueWatchingItem) : Event

        /** Пользователь открыл детали элемента продолжения просмотра. */
        data class ContinueWatchingDetailsSelected(val entry: HomeContinueWatchingItem) : Event

        data class HistorySelected(val entry: WatchHistoryEntry) : Event

        data class HistoryDetailsSelected(val animeId: Int) : Event

        /** Пользователь выбрал вкладку библиотеки. */
        data class TabSelected(val tab: LibraryTab) : Event

        /** Экран снова стал активным для пользователя. */
        data object ScreenResumed : Event

        /** Пользователь запросил повторную загрузку библиотеки. */
        data object RetrySelected : Event

        /** Пользователь удалил тайтл из указанной части библиотеки. */
        data class RemoveEntry(val animeId: Int, val target: LibraryRemoveTarget) : Event

        /** Пользователь скрыл запись продолжения просмотра. */
        data class RemoveWatchProgress(val entry: HomeContinueWatchingItem) : Event
    }

    sealed interface Effect : UiEffect {
        data object ItemRemoved : Effect
        data class ShowToast(val message: String) : Effect
    }
}
