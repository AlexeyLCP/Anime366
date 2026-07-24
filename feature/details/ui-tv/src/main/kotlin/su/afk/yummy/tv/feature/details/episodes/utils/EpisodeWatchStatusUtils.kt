package su.afk.yummy.tv.feature.details.episodes.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import su.afk.yummy.tv.core.model.anime.AnimeVideo
import su.afk.yummy.tv.core.model.anime.isMeaningfulProgress
import su.afk.yummy.tv.core.model.anime.isWatchedProgress
import su.afk.yummy.tv.core.model.anime.progress
import su.afk.yummy.tv.feature.details.R
import su.afk.yummy.tv.feature.details.episodes.model.EpisodeWatchStatus
import su.afk.yummy.tv.feature.details.model.DetailsWatchProgressIndex

internal fun List<AnimeVideo>.watchStatus(
    watchProgress: DetailsWatchProgressIndex,
): EpisodeWatchStatus {
    val best = watchProgress.bestFor(this)
        ?: return EpisodeWatchStatus.None

    if (!best.isMeaningfulProgress()) {
        return EpisodeWatchStatus.None
    }

    val progress = best.progress()
    return if (best.isWatchedProgress()) {
        EpisodeWatchStatus.Watched(
            positionMs = best.positionMs,
            durationMs = best.durationMs,
        )
    } else {
        EpisodeWatchStatus.InProgress(
            progress = progress,
            positionMs = best.positionMs,
            durationMs = best.durationMs,
        )
    }
}

/** Тайминг серии: «24:00» без прогресса и у досмотренных, «11:24 / 24:00» — у начатых. */
@Composable
internal fun EpisodeWatchStatus.durationLabel(fallbackDurationSeconds: Int?): String? {
    val fallbackMs = fallbackDurationSeconds?.takeIf { it > 0 }?.times(1_000L)
    return when (this) {
        EpisodeWatchStatus.None -> fallbackMs?.toWatchTimeString()

        is EpisodeWatchStatus.Watched ->
            (durationMs.takeIf { it > 0 } ?: fallbackMs)?.toWatchTimeString()

        is EpisodeWatchStatus.InProgress -> {
            val totalMs = durationMs.takeIf { it > 0 } ?: fallbackMs
            ?: return positionMs.toWatchTimeString()
            stringResource(
                R.string.details_episode_watch_timing,
                positionMs.toWatchTimeString(),
                totalMs.toWatchTimeString(),
            )
        }
    }
}

private fun Long.toWatchTimeString(): String {
    val totalSec = coerceAtLeast(0L) / 1000L
    val h = totalSec / 3600L
    val m = (totalSec % 3600L) / 60L
    val s = totalSec % 60L
    return if (h > 0) "%d:%02d:%02d".format(h, m, s) else "%d:%02d".format(m, s)
}
