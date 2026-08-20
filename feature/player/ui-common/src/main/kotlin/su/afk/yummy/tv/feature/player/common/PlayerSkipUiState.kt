package su.afk.yummy.tv.feature.player.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

/** Пропуск сегментов: подсветка кнопки, снекбар (3s) и уже пропущенные сегменты. */
@Stable
class PlayerSkipUiState internal constructor(
    private val scope: CoroutineScope,
    highlightedSkipKeyState: MutableState<String?>,
    snackbarTextState: MutableState<String?>,
    val dismissedSkipKeys: SnapshotStateList<String>,
) {
    var highlightedSkipKey: String? by highlightedSkipKeyState
    var snackbarText: String? by snackbarTextState
        private set

    private var snackbarJob: Job? = null

    fun showSnackbar(message: String) {
        snackbarText = message
        snackbarJob?.cancel()
        snackbarJob = scope.launch {
            delay(3.seconds)
            if (snackbarText == message) snackbarText = null
        }
    }

    fun cancel() {
        snackbarJob?.cancel()
    }
}

/** [resetKey] — идентификатор серии: при её смене пропущенные сегменты и снекбар сбрасываются. */
@Composable
fun rememberPlayerSkipUiState(resetKey: Any): PlayerSkipUiState {
    val scope = rememberCoroutineScope()
    val highlightedSkipKey = remember { mutableStateOf<String?>(null) }
    val snackbarText = remember(resetKey) { mutableStateOf<String?>(null) }
    val dismissedSkipKeys = remember(resetKey) { mutableStateListOf<String>() }
    return remember(snackbarText, dismissedSkipKeys) {
        PlayerSkipUiState(
            scope = scope,
            highlightedSkipKeyState = highlightedSkipKey,
            snackbarTextState = snackbarText,
            dismissedSkipKeys = dismissedSkipKeys,
        )
    }
}
