package su.afk.yummy.tv.feature.messages.dialogs

import androidx.lifecycle.SavedStateHandle
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import su.afk.yummy.tv.core.designsystem.presenter.baseViewModel.BaseViewModelNew
import su.afk.yummy.tv.core.error.IErrorHandlerUseCase
import su.afk.yummy.tv.core.error.storage.RetryStorage
import su.afk.yummy.tv.core.navigation.NavigationManager
import su.afk.yummy.tv.core.utils.PagedSource
import su.afk.yummy.tv.core.utils.pagingSource
import su.afk.yummy.tv.domain.account.usecase.ObserveAccountSessionUseCase
import su.afk.yummy.tv.domain.messages.MessagesMutationNotifier
import su.afk.yummy.tv.domain.messages.model.DialogSummary
import su.afk.yummy.tv.domain.messages.usecase.GetDialogsUseCase
import su.afk.yummy.tv.feature.account.IAccountNavigator
import su.afk.yummy.tv.feature.messages.IMessagesNavigator
import javax.inject.Inject

private const val DIALOGS_PAGE_SIZE = 20

@HiltViewModel
class DialogsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    override val errorHandler: IErrorHandlerUseCase,
    override val retryStorage: RetryStorage,
    private val nav: NavigationManager,
    private val navigator: IMessagesNavigator,
    private val accountNavigator: IAccountNavigator,
    private val observeAccountSession: ObserveAccountSessionUseCase,
    private val getDialogs: GetDialogsUseCase,
    mutationNotifier: MessagesMutationNotifier,
) : BaseViewModelNew<DialogsState.State, DialogsState.Event, DialogsState.Effect>(savedStateHandle) {
    private var pagedSource: PagedSource<DialogSummary>? = null

    override fun createInitialState() = DialogsState.State()

    init {
        observeAccountSession()
            .onEach { session ->
                val shouldCreateFlow = session.isAuthorized && !currentState.isAuthorized
                setState {
                    copy(
                        isAuthResolved = true,
                        isAuthorized = session.isAuthorized,
                        dialogs = if (shouldCreateFlow) createDialogsFlow() else dialogs,
                    )
                }
            }
            .launchIn(viewModelScope)
        mutationNotifier.version
            .drop(1)
            .onEach { pagedSource?.invalidate() }
            .launchIn(viewModelScope)
    }

    override fun onEvent(event: DialogsState.Event) {
        when (event) {
            DialogsState.Event.BackSelected -> nav.back()
            DialogsState.Event.LoginSelected -> nav.navigate(accountNavigator.getAccountDest())
            is DialogsState.Event.DialogSelected -> if (event.userId > 0) {
                nav.navigate(navigator.chat(event.userId))
            }
        }
    }

    private fun createDialogsFlow() =
        pagingSource(viewModelScope, pageSize = DIALOGS_PAGE_SIZE) { limit, offset ->
            getDialogs(limit, offset)
        }.also { pagedSource = it }.flow
}
