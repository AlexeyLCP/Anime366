package su.afk.yummy.tv.feature.player.common.utils

import androidx.annotation.StringRes
import su.afk.yummy.tv.feature.player.PlayerSkips
import su.afk.yummy.tv.feature.player.common.model.PlayerActiveSkip
import su.afk.yummy.tv.feature.player.model.PlayerSkipType
import su.afk.yummy.tv.feature.player.presentation.R

fun currentSkip(
    skips: PlayerSkips,
    positionMs: Long,
    dismissedKeys: List<String>,
): PlayerActiveSkip? =
    listOfNotNull(
        skips.opening?.let {
            PlayerActiveSkip(
                "opening:${it.startMs}:${it.endMs}",
                PlayerSkipType.Opening,
                it
            )
        },
        skips.ending?.let {
            PlayerActiveSkip(
                "ending:${it.startMs}:${it.endMs}",
                PlayerSkipType.Ending,
                it
            )
        },
    ).firstOrNull { skip ->
        skip.key !in dismissedKeys && positionMs in skip.segment.startMs..skip.segment.endMs
    }

@StringRes
fun PlayerSkipType.skippedMessageRes(): Int =
    when (this) {
        PlayerSkipType.Opening -> R.string.player_opening_skipped
        PlayerSkipType.Ending -> R.string.player_ending_skipped
    }
