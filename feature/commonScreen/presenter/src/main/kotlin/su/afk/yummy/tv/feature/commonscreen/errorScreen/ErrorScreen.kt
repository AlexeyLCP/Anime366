package su.afk.yummy.tv.feature.commonscreen.errorScreen

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import kotlinx.coroutines.flow.Flow
import su.afk.yummy.tv.core.designsystem.baseScreen.BaseScreen
import su.afk.yummy.tv.feature.commonscreen.errorScreen.ErrorScreenState.Effect
import su.afk.yummy.tv.feature.commonscreen.errorScreen.ErrorScreenState.Event
import su.afk.yummy.tv.feature.commonscreen.errorScreen.ErrorScreenState.State

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ErrorScreen(
    state: State,
    onEvent: (Event) -> Unit,
    effect: Flow<Effect>,
) {
    BaseScreen(
        error = state.error,
        onRetry = { onEvent(Event.Retry) },
    ) {}
}
