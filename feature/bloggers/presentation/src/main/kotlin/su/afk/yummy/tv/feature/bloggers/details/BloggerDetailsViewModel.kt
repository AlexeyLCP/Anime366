package su.afk.yummy.tv.feature.bloggers.details

import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import su.afk.yummy.tv.core.designsystem.presenter.baseViewModel.BaseViewModelNew
import su.afk.yummy.tv.core.error.IErrorHandlerUseCase
import su.afk.yummy.tv.core.error.StringProvider
import su.afk.yummy.tv.core.error.storage.RetryStorage
import su.afk.yummy.tv.core.navigation.NavigationManager
import su.afk.yummy.tv.core.preferences.settings.SettingsStore
import su.afk.yummy.tv.core.utils.runSuspendCatching
import su.afk.yummy.tv.domain.bloggers.usecase.GetBloggerDetailsUseCase
import su.afk.yummy.tv.domain.bloggers.usecase.GetBloggerVideosUseCase
import su.afk.yummy.tv.domain.bloggers.usecase.SetBloggerSubscribedUseCase
import su.afk.yummy.tv.feature.bloggers.BLOGGER_VIDEOS_PAGE_SIZE
import su.afk.yummy.tv.feature.bloggers.IBloggerVideosNavigator
import su.afk.yummy.tv.feature.bloggers.presentation.R

@HiltViewModel(assistedFactory = BloggerDetailsViewModel.Factory::class)
class BloggerDetailsViewModel @AssistedInject constructor(
    @Assisted private val bloggerId: Int,
    override val errorHandler: IErrorHandlerUseCase,
    override val retryStorage: RetryStorage,
    private val nav: NavigationManager,
    private val navigator: IBloggerVideosNavigator,
    private val getDetails: GetBloggerDetailsUseCase,
    private val getVideos: GetBloggerVideosUseCase,
    private val setSubscribed: SetBloggerSubscribedUseCase,
    private val strings: StringProvider,
    settingsStore: SettingsStore,
) : BaseViewModelNew<BloggerDetailsState.State, BloggerDetailsState.Event, BloggerDetailsState.Effect>() {
    override fun createInitialState() = BloggerDetailsState.State()

    init {
        settingsStore.yaniUserId.onEach { setState { copy(currentUserId = it) } }
            .launchIn(viewModelScope)
        load()
    }

    override fun onEvent(event: BloggerDetailsState.Event) {
        when (event) {
            BloggerDetailsState.Event.BackSelected -> nav.back()
            BloggerDetailsState.Event.RetrySelected -> load()
            BloggerDetailsState.Event.SubscribeSelected -> toggleSubscription()
            is BloggerDetailsState.Event.VideoSelected -> nav.navigate(navigator.video(event.videoId))
        }
    }

    private fun load() = viewModelScope.launch {
        setState { copy(loading = true, error = null) }
        runSuspendCatching {
            val details = async { getDetails(bloggerId) }
            val videos =
                async { getVideos(bloggerId = bloggerId, limit = BLOGGER_VIDEOS_PAGE_SIZE) }
            details.await() to videos.await()
        }.fold(
            { (blogger, videos) ->
                setState {
                    copy(
                        blogger = blogger,
                        videos = videos.toImmutableList(),
                        loading = false
                    )
                }
            },
            { error ->
                setState {
                    copy(
                        loading = false,
                        error = errorHandler.parse(error, navigate = false).message,
                    )
                }
            },
        )
    }

    private fun toggleSubscription() {
        if (currentState.currentUserId <= 0) {
            setEffect(BloggerDetailsState.Effect.ShowToast(strings.get(R.string.bloggers_auth_required)))
            return
        }
        val old = currentState.blogger ?: return
        if (currentState.subscribing) return
        val target = !old.isSubscribed
        setState { copy(blogger = old.copy(isSubscribed = target), subscribing = true) }
        viewModelScope.launch {
            runSuspendCatching { setSubscribed(bloggerId, target) }.fold(
                { subscribers ->
                    setState {
                        copy(
                            blogger = blogger?.copy(
                                subscribers = subscribers,
                                isSubscribed = target
                            ), subscribing = false
                        )
                    }
                },
                { error ->
                    setState { copy(blogger = old, subscribing = false) }
                    setEffect(
                        BloggerDetailsState.Effect.ShowToast(
                            errorHandler.parse(error, navigate = false).message
                        )
                    )
                },
            )
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(bloggerId: Int): BloggerDetailsViewModel
    }
}
