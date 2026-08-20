package su.afk.yummy.tv.feature.player.mobile.view

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.media3.common.Player
import kotlinx.coroutines.delay
import su.afk.yummy.tv.feature.player.common.PlayerProgressReporter
import su.afk.yummy.tv.feature.player.common.utils.calculateBufferedProgress
import kotlin.time.Duration.Companion.seconds

/** Секундный цикл: notify позиции, буферизация и сохранение прогресса каждые 10 секунд. */
@Composable
internal fun MobilePlayerProgressPollingEffect(
    player: Player,
    episodeKey: String,
    isMediaReady: Boolean,
    reporter: PlayerProgressReporter,
    isSeeking: () -> Boolean,
    currentPositionMs: () -> Long,
    fallbackDurationMs: () -> Long,
    onBufferedProgressChange: (Float) -> Unit,
) {
    val currentIsSeeking by rememberUpdatedState(isSeeking)
    val currentPosition by rememberUpdatedState(currentPositionMs)
    val currentFallbackDuration by rememberUpdatedState(fallbackDurationMs)
    val currentOnBufferedProgressChange by rememberUpdatedState(onBufferedProgressChange)

    LaunchedEffect(player, episodeKey, isMediaReady) {
        while (true) {
            var position = currentPosition()
            var dur = currentFallbackDuration()
            if (!currentIsSeeking()) {
                position = player.currentPosition.coerceAtLeast(0)
                dur = player.duration.takeIf { it > 0 } ?: 0L
                reporter.notifyPositionChanged(position, dur)
            }
            currentOnBufferedProgressChange(
                calculateBufferedProgress(
                    bufferedPosition = player.bufferedPosition,
                    currentPosition = position,
                    duration = dur,
                )
            )
            val now = System.currentTimeMillis()
            if (dur > 0 && now - reporter.lastSaveTimeMs >= 10_000L) {
                reporter.saveProgress(position, dur)
            }
            delay(1.seconds)
        }
    }
}
