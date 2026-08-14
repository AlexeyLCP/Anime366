package su.afk.yummy.tv.feature.details.trailers

import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch
import su.afk.yummy.tv.core.designsystem.presenter.baseViewModel.BaseViewModelNew
import su.afk.yummy.tv.core.error.api.IErrorHandlerUseCase
import su.afk.yummy.tv.core.error.api.RetryStorage
import su.afk.yummy.tv.core.navigation.manager.INavigationManager
import su.afk.yummy.tv.domain.anime.usecase.GetAnimeTrailersUseCase
import su.afk.yummy.tv.feature.details.DetailsAnalytics

@HiltViewModel(assistedFactory = TrailersViewModel.Factory::class)
class TrailersViewModel @AssistedInject internal constructor(
    @Assisted private val animeId: Int,
    override val errorHandler: IErrorHandlerUseCase,
    override val retryStorage: RetryStorage,
    private val nav: INavigationManager,
    private val getAnimeTrailers: GetAnimeTrailersUseCase,
    private val analytics: DetailsAnalytics,
) : BaseViewModelNew<TrailersState.State, TrailersState.Event, TrailersState.Effect>() {

    @AssistedFactory
    interface Factory {
        fun create(animeId: Int): TrailersViewModel
    }

    override fun createInitialState() = TrailersState.State()

    init {
        analytics.eventTrailersScreenOpened(animeId)
        viewModelScope.launch { load() }
    }

    override fun onEvent(event: TrailersState.Event) {
        when (event) {
            TrailersState.Event.BackSelected -> nav.back()
            TrailersState.Event.RetrySelected -> viewModelScope.launch { load() }
        }
    }

    private suspend fun load() {
        setState { copy(isLoading = true, error = null) }
        runCatching { getAnimeTrailers(animeId) }.fold(
            onSuccess = { trailers ->
                setState { copy(isLoading = false, trailers = trailers.toImmutableList()) }
            },
            onFailure = { setState { copy(isLoading = false, error = it.message) } },
        )
    }
}
