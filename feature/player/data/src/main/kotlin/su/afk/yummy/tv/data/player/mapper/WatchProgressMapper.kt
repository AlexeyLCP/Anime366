package su.afk.yummy.tv.data.player.mapper

import su.afk.yummy.tv.core.model.anime.AnimeWatchProgress
import su.afk.yummy.tv.core.storage.watchprogress.WatchProgressEntry

internal fun WatchProgressEntry.toDomain(): AnimeWatchProgress =
    AnimeWatchProgress(
        animeId = animeId,
        episode = episode,
        videoId = videoId,
        episodeUrl = episodeUrl,
        positionMs = positionMs,
        durationMs = durationMs,
        updatedAt = updatedAt,
        animeTitle = animeTitle,
        posterUrl = posterUrl,
        playerName = playerName,
        dubbing = dubbing,
        screenshotUrl = screenshotUrl,
    )
