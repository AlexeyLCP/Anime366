package su.afk.yummy.tv.feature.bloggers.list

import androidx.lifecycle.SavedStateHandle
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import su.afk.yummy.tv.core.designsystem.presenter.baseViewModel.BaseViewModelNew
import su.afk.yummy.tv.core.error.IErrorHandlerUseCase
import su.afk.yummy.tv.core.error.storage.RetryStorage
import su.afk.yummy.tv.core.navigation.NavigationManager
import su.afk.yummy.tv.core.utils.pagingFlow
import su.afk.yummy.tv.core.utils.runSuspendCatching
import su.afk.yummy.tv.domain.bloggers.model.BloggerVideoSort
import su.afk.yummy.tv.domain.bloggers.usecase.GetAnimeBloggerVideosUseCase
import su.afk.yummy.tv.domain.bloggers.usecase.GetBloggerVideosUseCase
import su.afk.yummy.tv.domain.bloggers.usecase.GetBloggersDirectoryUseCase
import su.afk.yummy.tv.feature.bloggers.BLOGGER_VIDEOS_PAGE_SIZE
import su.afk.yummy.tv.feature.bloggers.IBloggerVideosNavigator

private const val ALL_CATEGORY_ID = "all"

@HiltViewModel(assistedFactory = BloggerVideosListViewModel.Factory::class)
class BloggerVideosListViewModel @AssistedInject constructor(
    savedStateHandle: SavedStateHandle,
    override val errorHandler: IErrorHandlerUseCase,
    override val retryStorage: RetryStorage,
    private val nav: NavigationManager,
    private val getVideos: GetBloggerVideosUseCase,
    private val getAnimeVideos: GetAnimeBloggerVideosUseCase,
    private val getDirectory: GetBloggersDirectoryUseCase,
    private val bloggerNavigator: IBloggerVideosNavigator,
    @Assisted private val animeId: Int?,
) : BaseViewModelNew<BloggerVideosListState.State, BloggerVideosListState.Event, BloggerVideosListState.Effect>(
    savedStateHandle
) {
    override fun createInitialState() = BloggerVideosListState.State(
        animeId = animeId,
        videos = createFlow(ALL_CATEGORY_ID, bloggerId = null, sort = BloggerVideoSort.NEW),
    )

    init {
        if (animeId == null) loadDirectory()
    }

    override fun onEvent(event: BloggerVideosListState.Event) {
        when (event) {
            BloggerVideosListState.Event.BackSelected -> nav.back()
            is BloggerVideosListState.Event.VideoSelected -> nav.navigate(
                bloggerNavigator.video(event.videoId)
            )

            is BloggerVideosListState.Event.BloggerDetailsSelected -> nav.navigate(
                bloggerNavigator.blogger(event.bloggerId)
            )

            is BloggerVideosListState.Event.CategorySelected -> {
                setState { copy(selectedCategory = event.id) }
                reloadVideos()
            }

            is BloggerVideosListState.Event.BloggerSelected -> {
                setState { copy(selectedBloggerId = event.id) }
                reloadVideos()
            }

            is BloggerVideosListState.Event.SortSelected -> {
                setState { copy(sort = event.sort) }
                reloadVideos()
            }

            BloggerVideosListState.Event.FiltersReset -> {
                setState { copy(selectedCategory = ALL_CATEGORY_ID, selectedBloggerId = null) }
                reloadVideos()
            }
        }
    }

    private fun loadDirectory() {
        viewModelScope.launch {
            runSuspendCatching { getDirectory() }.onSuccess { directory ->
                setState { copy(categories = directory.categories, bloggers = directory.bloggers) }
            }
        }
    }

    private fun reloadVideos() {
        if (animeId != null) return
        setState { copy(videos = createFlow(selectedCategory, selectedBloggerId, sort)) }
    }

    private fun createFlow(category: String, bloggerId: Int?, sort: BloggerVideoSort) =
        pagingFlow(viewModelScope, pageSize = BLOGGER_VIDEOS_PAGE_SIZE) { limit, offset ->
            if (animeId != null) {
                getAnimeVideos(animeId, limit, offset)
            } else {
                getVideos(
                    category = category,
                    bloggerId = bloggerId,
                    sort = sort,
                    limit = limit,
                    offset = offset,
                )
            }
        }

    @AssistedFactory
    interface Factory {
        fun create(animeId: Int?): BloggerVideosListViewModel
    }
}
