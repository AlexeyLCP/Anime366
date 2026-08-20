package su.afk.yummy.tv.feature.posts.list

import androidx.compose.runtime.Immutable
import androidx.paging.PagingData
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import su.afk.yummy.tv.core.mvi.UiEffect
import su.afk.yummy.tv.core.mvi.UiEvent
import su.afk.yummy.tv.core.mvi.UiState
import su.afk.yummy.tv.domain.posts.model.PostCategory
import su.afk.yummy.tv.domain.posts.model.PostSort
import su.afk.yummy.tv.domain.posts.model.PostSummary

class PostsListState {
    @Immutable
    data class State(
        val posts: Flow<PagingData<PostSummary>> = flowOf(PagingData.empty()),
        val categories: ImmutableList<PostCategory> = persistentListOf(),
        val selectedCategory: String? = null,
        val sort: PostSort = PostSort.NEW,
        val categoriesLoading: Boolean = true,
    ) : UiState

    sealed interface Event : UiEvent {
        data class PostSelected(val postId: Int) : Event
        data class CategorySelected(val uri: String?) : Event
        data class SortSelected(val sort: PostSort) : Event
    }

    sealed interface Effect : UiEffect
}
