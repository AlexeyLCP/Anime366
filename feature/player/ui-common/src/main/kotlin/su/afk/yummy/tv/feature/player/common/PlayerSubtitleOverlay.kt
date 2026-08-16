package su.afk.yummy.tv.feature.player.common

import android.graphics.Color
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
import androidx.media3.common.text.Cue
import androidx.media3.common.text.CueGroup
import androidx.media3.ui.CaptionStyleCompat
import androidx.media3.ui.SubtitleView
import su.afk.yummy.tv.core.preferences.settings.model.PlayerSubtitleStyleSettings

/**
 * Рендер текущих субтитров поверх видео. Player сам решает, есть ли активная text-дорожка
 * (см. [PlayerTrackSelectionState.selectText]) — здесь только отображение [Player.Listener.onCues]
 * и пользовательское оформление из [style]. Явные line/position реплик (в т.ч. \pos из
 * AllohaAssPositionFix) сбрасываем — иначе они перебивают [style.offset], заданный пользователем.
 */
@Composable
fun PlayerSubtitleOverlay(
    player: Player?,
    style: PlayerSubtitleStyleSettings,
    modifier: Modifier = Modifier,
) {
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
        factory = { context ->
            SubtitleView(context).apply {
                // Alloha отдаёт ASS/SSA со своими размерами и цветами — без этого пользовательские
                // настройки просто игнорировались бы встроенными стилями дорожки.
                setApplyEmbeddedStyles(false)
                setApplyEmbeddedFontSizes(false)
            }
        },
        update = { view ->
            view.setCues(cueGroup?.cues.orEmpty().map { it.withDefaultPosition() })
            view.setFractionalTextSize(
                SubtitleView.DEFAULT_TEXT_SIZE_FRACTION * style.textSize.scale
            )
            view.setBottomPaddingFraction(style.offset.bottomFraction)
            view.setStyle(
                CaptionStyleCompat(
                    style.textColor.argb,
                    style.background.argb,
                    Color.TRANSPARENT,
                    CaptionStyleCompat.EDGE_TYPE_OUTLINE,
                    Color.BLACK,
                    null,
                )
            )
        },
    )
}

/**
 * Сбрасывает line/position текстовых cue на DIMEN_UNSET, чтобы [SubtitleView] позиционировал их
 * через `bottomPaddingFraction`, а не через координаты, зашитые в исходной дорожке. Битмап-cue
 * (например, DVB) не трогаем — там позиция обычно осмысленная и не сводится к «снизу по центру».
 */
private fun Cue.withDefaultPosition(): Cue {
    if (bitmap != null) return this
    return buildUpon()
        .setLine(Cue.DIMEN_UNSET, Cue.TYPE_UNSET)
        .setPosition(Cue.DIMEN_UNSET)
        .build()
}
