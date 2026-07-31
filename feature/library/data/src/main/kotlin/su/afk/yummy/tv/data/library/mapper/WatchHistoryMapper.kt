package su.afk.yummy.tv.data.library.mapper

import su.afk.yummy.tv.core.utils.toHttpsUrlOrNull
import su.afk.yummy.tv.data.library.dto.YaniWatchHistoryDto
import su.afk.yummy.tv.domain.library.model.WatchHistoryEntry

internal fun YaniWatchHistoryDto.toDomainOrNull(): WatchHistoryEntry? {
    if (animeId <= 0 || videoId <= 0) return null
    return WatchHistoryEntry(
        animeId = animeId,
        videoId = videoId,
        animeUrl = animeUrl,
        title = title.ifBlank { animeUrl },
        episode = episode ?: screenshot?.episode.orEmpty(),
        episodeTitle = episodeTitle,
        posterUrl = poster?.run { mega ?: huge ?: big ?: medium ?: small ?: fullsize }
            .toHttpsUrlOrNull(),
        screenshotUrl = screenshot?.sizes?.run { full ?: small }.toHttpsUrlOrNull(),
        watchedAtSeconds = date,
        positionSeconds = endTime.coerceAtLeast(0),
        durationSeconds = duration.coerceAtLeast(0),
        dubbing = dubbing,
        player = player,
    )
}
