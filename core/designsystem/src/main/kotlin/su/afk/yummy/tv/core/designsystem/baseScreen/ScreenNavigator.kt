package su.afk.yummy.tv.core.designsystem.baseScreen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.Flow
import su.afk.yummy.tv.core.mvi.BaseViewModel
import su.afk.yummy.tv.core.mvi.UiEffect
import su.afk.yummy.tv.core.mvi.UiEvent
import su.afk.yummy.tv.core.mvi.UiState

/** Подписывает [content] на состояние и эффекты [viewModel] с учётом жизненного цикла экрана. */
@Composable
fun <S : UiState, E : UiEvent, F : UiEffect> ScreenNavigator(
    viewModel: BaseViewModel<S, E, F>,
    content: @Composable (state: S, effect: Flow<F>, onEventSent: (E) -> Unit) -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    content(state, viewModel.effect, viewModel::setEvent)
}
