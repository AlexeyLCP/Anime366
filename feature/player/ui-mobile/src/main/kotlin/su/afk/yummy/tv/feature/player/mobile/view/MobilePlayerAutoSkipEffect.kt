package su.afk.yummy.tv.feature.player.mobile.view

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import su.afk.yummy.tv.feature.player.common.model.PlayerActiveSkip

/** Авто-скип активного сегмента, когда включена соответствующая настройка. */
@Composable
internal fun MobilePlayerAutoSkipEffect(
    activeSkip: PlayerActiveSkip?,
    autoSkipOpeningsEndings: Boolean,
    onSkipActiveSegment: () -> Unit,
) {
    val currentOnSkipActiveSegment by rememberUpdatedState(onSkipActiveSegment)

    LaunchedEffect(activeSkip?.key, autoSkipOpeningsEndings) {
        if (autoSkipOpeningsEndings && activeSkip != null) currentOnSkipActiveSegment()
    }
}
