package su.afk.yummy.tv.feature.details.mobile.episodes.utils

import su.afk.yummy.tv.core.model.anime.AnimeVideo
import su.afk.yummy.tv.core.model.anime.isMeaningfulProgress
import su.afk.yummy.tv.core.model.anime.isWatchedProgress
import su.afk.yummy.tv.core.model.anime.progress
import su.afk.yummy.tv.feature.details.mobile.episodes.model.EpisodeMobileWatchStatus
import su.afk.yummy.tv.feature.details.model.DetailsWatchProgressIndex

internal fun List<AnimeVideo>.mobileWatchStatus(
    watchProgress: DetailsWatchProgressIndex,
): EpisodeMobileWatchStatus {
    val best = watchProgress.bestFor(this)
        ?: return EpisodeMobileWatchStatus.None

    if (!best.isMeaningfulProgress()) {
        return EpisodeMobileWatchStatus.None
    }

    val progress = best.progress()
    return if (best.isWatchedProgress()) {
        EpisodeMobileWatchStatus.Watched(
            positionMs = best.positionMs,
            durationMs = best.durationMs,
        )
    } else {
        EpisodeMobileWatchStatus.InProgress(
            progress = progress,
            positionMs = best.positionMs,
            durationMs = best.durationMs,
        )
    }
}

/** Тайминг серии: «24:00» без прогресса и у досмотренных, «11:24 / 24:00» — у начатых. */
internal fun EpisodeMobileWatchStatus.durationLabel(fallbackDurationSeconds: Int?): String? {
    val fallbackMs = fallbackDurationSeconds?.takeIf { it > 0 }?.times(1_000L)
    return when (this) {
        EpisodeMobileWatchStatus.None -> fallbackMs?.toMobileWatchTimeString()

        is EpisodeMobileWatchStatus.Watched ->
            (durationMs.takeIf { it > 0 } ?: fallbackMs)?.toMobileWatchTimeString()

        is EpisodeMobileWatchStatus.InProgress -> {
            val totalMs = durationMs.takeIf { it > 0 } ?: fallbackMs
            ?: return positionMs.toMobileWatchTimeString()
            "${positionMs.toMobileWatchTimeString()} / ${totalMs.toMobileWatchTimeString()}"
        }
    }
}

private fun Long.toMobileWatchTimeString(): String {
    val totalSec = coerceAtLeast(0L) / 1000L
    val h = totalSec / 3600L
    val m = (totalSec % 3600L) / 60L
    val s = totalSec % 60L
    return if (h > 0) "%d:%02d:%02d".format(h, m, s) else "%d:%02d".format(m, s)
}
