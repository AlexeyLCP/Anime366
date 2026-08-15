package su.afk.yummy.tv.feature.player.common

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.Player
import androidx.media3.common.text.CueGroup
import androidx.media3.ui.SubtitleView

/**
 * Рендер текущих субтитров поверх видео. Player сам решает, есть ли активная text-дорожка
 * (см. [PlayerTrackSelectionState.selectText]) — здесь только отображение [Player.Listener.onCues].
 */
@Composable
fun PlayerSubtitleOverlay(player: Player?, modifier: Modifier = Modifier) {
    var cueGroup by remember { mutableStateOf<CueGroup?>(null) }

    DisposableEffect(player) {
        if (player == null) {
            onDispose {}
        } else {
            cueGroup = player.currentCues
            val listener = object : Player.Listener {
                override fun onCues(cues: CueGroup) {
                    cueGroup = cues
                }
            }
            player.addListener(listener)
            onDispose { player.removeListener(listener) }
        }
    }

    AndroidView(
        modifier = modifier.fillMaxSize(),
        factory = { context -> SubtitleView(context) },
        update = { view -> view.setCues(cueGroup?.cues.orEmpty()) },
    )
}
