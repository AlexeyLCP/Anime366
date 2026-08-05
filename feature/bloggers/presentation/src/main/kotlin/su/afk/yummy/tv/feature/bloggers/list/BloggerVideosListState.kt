package su.afk.yummy.tv.feature.bloggers.list

import androidx.compose.runtime.Immutable
import androidx.paging.PagingData
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import su.afk.yummy.tv.core.designsystem.presenter.baseViewModel.UiEffect
import su.afk.yummy.tv.core.designsystem.presenter.baseViewModel.UiEvent
import su.afk.yummy.tv.core.designsystem.presenter.baseViewModel.UiState
import su.afk.yummy.tv.domain.bloggers.model.Blogger
import su.afk.yummy.tv.domain.bloggers.model.BloggerVideo
import su.afk.yummy.tv.domain.bloggers.model.BloggerVideoCategory
import su.afk.yummy.tv.domain.bloggers.model.BloggerVideoSort

class BloggerVideosListState {
    @Immutable
    data class State(
        val animeId: Int? = null,
        val videos: Flow<PagingData<BloggerVideo>> = flowOf(PagingData.empty()),
        val categories: ImmutableList<BloggerVideoCategory> = persistentListOf(),
        val bloggers: ImmutableList<Blogger> = persistentListOf(),
        val selectedCategory: String = "all",
        val selectedBloggerId: Int? = null,
        val sort: BloggerVideoSort = BloggerVideoSort.NEW,
    ) : UiState

    sealed interface Event : UiEvent {
        data object BackSelected : Event
        data class VideoSelected(val videoId: Int) : Event
        data class BloggerDetailsSelected(val bloggerId: Int) : Event
        data class CategorySelected(val id: String) : Event
        data class BloggerSelected(val id: Int?) : Event
        data class SortSelected(val sort: BloggerVideoSort) : Event
        data object FiltersReset : Event
    }

    sealed interface Effect : UiEffect
}
