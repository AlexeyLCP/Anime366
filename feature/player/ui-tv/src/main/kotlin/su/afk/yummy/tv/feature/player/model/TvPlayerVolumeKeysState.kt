package su.afk.yummy.tv.feature.player.model

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration

/**
 * Индикатор громкости при перехвате кнопок пульта: показывает текущий уровень в процентах
 * и сам скрывается через [indicatorDuration]. Логика шага (системная или «продвинутая»
 * внутренняя громкость) решается на стороне вызывающего кода.
 */
@Stable
internal class TvPlayerVolumeKeysState(
    private val scope: CoroutineScope,
    private val indicatorDuration: Duration,
) {
    var indicatorText: String? by mutableStateOf(null)
        private set

    private var hideJob: Job? = null

    /** Показать уровень [percent] % и запланировать скрытие индикатора. */
    fun show(percent: Int) {
        indicatorText = "$percent%"
        hideJob?.cancel()
        hideJob = scope.launch {
            delay(indicatorDuration)
            indicatorText = null
        }
    }
}

@Composable
internal fun rememberTvPlayerVolumeKeysState(
    indicatorDuration: Duration,
): TvPlayerVolumeKeysState {
    val scope = rememberCoroutineScope()
    return remember {
        TvPlayerVolumeKeysState(
            scope = scope,
            indicatorDuration = indicatorDuration,
        )
    }
}
