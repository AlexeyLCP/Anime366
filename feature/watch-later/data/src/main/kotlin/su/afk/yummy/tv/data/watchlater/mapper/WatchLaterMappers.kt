package su.afk.yummy.tv.data.watchlater.mapper

import su.afk.yummy.tv.core.storage.watchlater.WatchLaterEntry
import su.afk.yummy.tv.domain.watchlater.model.WatchLaterItem

internal fun WatchLaterEntry.toDomain(): WatchLaterItem = WatchLaterItem(
    animeId = animeId,
    episode = episode,
    animeTitle = animeTitle,
    posterUrl = posterUrl,
    screenshotUrl = screenshotUrl,
    addedAt = addedAt,
)
