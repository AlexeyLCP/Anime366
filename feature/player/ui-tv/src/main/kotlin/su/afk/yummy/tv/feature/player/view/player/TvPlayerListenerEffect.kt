package su.afk.yummy.tv.feature.player.view.player

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import su.afk.yummy.tv.feature.player.PlayerState
import su.afk.yummy.tv.feature.player.common.PlayerAutoHideController
import su.afk.yummy.tv.feature.player.common.PlayerStepSeekToastState
import su.afk.yummy.tv.feature.player.common.toPlaybackErrorEvent
import su.afk.yummy.tv.feature.player.common.utils.positionSnapshot
import su.afk.yummy.tv.feature.player.model.TvPlayerSkipUiState

/**
 * Player.Listener TV-плеера: play/pause, завершение эпизода, ошибки.
 * Промптами конца эпизода владеет handleEpisodeEnd в TvExoPlayerView,
 * выгрузкой сервиса и финальным сохранением — TvPlayerLifecycleEffect.
 */
@Composable
internal fun TvPlayerListenerEffect(
    player: Player,
    autoHide: PlayerAutoHideController,
    skipUi: TvPlayerSkipUiState,
    stepSeekToast: PlayerStepSeekToastState,
    fallbackDurationMs: () -> Long,
    wantsPlay: () -> Boolean,
    onWantsPlayChanged: (Boolean) -> Unit,
    onEpisodeEnd: (positionMs: Long, durationMs: Long) -> Unit,
    onEvent: (PlayerState.Event) -> Unit,
) {
    val currentFallbackDuration by rememberUpdatedState(fallbackDurationMs)
    val currentWantsPlay by rememberUpdatedState(wantsPlay)
    val currentOnWantsPlayChanged by rememberUpdatedState(onWantsPlayChanged)
    val currentOnEpisodeEnd by rememberUpdatedState(onEpisodeEnd)
    val currentOnEvent by rememberUpdatedState(onEvent)
    val currentStepSeekToast by rememberUpdatedState(stepSeekToast)

    DisposableEffect(player) {
        player.playWhenReady = currentWantsPlay()
        val listener = object : Player.Listener {
            override fun onPlayWhenReadyChanged(pwr: Boolean, reason: Int) {
                currentOnWantsPlayChanged(pwr)
                if (pwr) autoHide.schedule() else autoHide.cancel()
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == Player.STATE_READY) {
                    currentOnEvent(PlayerState.Event.PlaybackReady)
                }
                if (playbackState == Player.STATE_ENDED) {
                    val snapshot = player.positionSnapshot(currentFallbackDuration())
                    currentOnEpisodeEnd(snapshot.positionMs, snapshot.durationMs)
                }
            }

            override fun onPlayerError(error: PlaybackException) {
                val position = player.currentPosition.coerceAtLeast(0L)
                currentOnEvent(error.toPlaybackErrorEvent(position))
            }
        }
        player.addListener(listener)
        if (currentWantsPlay()) autoHide.schedule() else autoHide.cancel()
        onDispose {
            autoHide.cancel()
            skipUi.cancel()
            currentStepSeekToast.cancel()
            player.removeListener(listener)
        }
    }
}
