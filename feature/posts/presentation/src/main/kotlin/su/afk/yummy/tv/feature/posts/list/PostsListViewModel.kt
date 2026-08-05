package su.afk.yummy.tv.feature.posts.list

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch
import su.afk.yummy.tv.core.designsystem.presenter.baseViewModel.BaseViewModelNew
import su.afk.yummy.tv.core.error.IErrorHandlerUseCase
import su.afk.yummy.tv.core.error.storage.RetryStorage
import su.afk.yummy.tv.core.navigation.NavigationManager
import su.afk.yummy.tv.core.utils.pagingFlow
import su.afk.yummy.tv.domain.posts.model.PostSort
import su.afk.yummy.tv.domain.posts.usecase.GetPostCategoriesUseCase
import su.afk.yummy.tv.domain.posts.usecase.GetPostsUseCase
import su.afk.yummy.tv.feature.posts.IPostsNavigator
import javax.inject.Inject

@HiltViewModel
class PostsListViewModel @Inject constructor(
    override val errorHandler: IErrorHandlerUseCase,
    override val retryStorage: RetryStorage,
    private val nav: NavigationManager,
    private val navigator: IPostsNavigator,
    private val getPostCategories: GetPostCategoriesUseCase,
    private val getPosts: GetPostsUseCase,
) : BaseViewModelNew<PostsListState.State, PostsListState.Event, PostsListState.Effect>() {
    override fun createInitialState() = PostsListState.State(posts = createFlow(null, PostSort.NEW))

    init {
        loadCategories()
    }

    override fun onEvent(event: PostsListState.Event) {
        when (event) {
            is PostsListState.Event.PostSelected -> nav.navigate(navigator.details(event.postId))
            is PostsListState.Event.CategorySelected -> if (event.uri != currentState.selectedCategory) {
                setState { copy(selectedCategory = event.uri, posts = createFlow(event.uri, sort)) }
            }

            is PostsListState.Event.SortSelected -> if (event.sort != currentState.sort) {
                setState {
                    copy(
                        sort = event.sort,
                        posts = createFlow(selectedCategory, event.sort)
                    )
                }
            }
        }
    }

    private fun loadCategories() = viewModelScope.launch {
        runCatching { getPostCategories() }.fold(
            { loaded ->
                setState {
                    copy(
                        categories = loaded.toImmutableList(),
                        categoriesLoading = false
                    )
                }
            },
            { setState { copy(categoriesLoading = false) } },
        )
    }

    private fun createFlow(category: String?, sort: PostSort) =
        pagingFlow(viewModelScope) { limit, offset ->
            getPosts(category, sort.apiValue, limit, offset)
        }
}
