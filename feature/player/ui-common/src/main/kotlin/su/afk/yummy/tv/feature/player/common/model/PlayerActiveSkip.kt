package su.afk.yummy.tv.feature.player.common.model

import androidx.compose.runtime.Immutable
import su.afk.yummy.tv.feature.player.PlayerSkipSegment
import su.afk.yummy.tv.feature.player.model.PlayerSkipType

/** Сегмент опенинга/эндинга, внутри которого сейчас находится позиция воспроизведения. */
@Immutable
data class PlayerActiveSkip(
    val key: String,
    val type: PlayerSkipType,
    val segment: PlayerSkipSegment,
)
