package su.afk.yummy.tv.domain.library.model

data class WatchHistoryEntry(
    val animeId: Int,
    val animeUrl: String,
    val title: String,
    val episode: String,
    val posterUrl: String?,
    val screenshotUrl: String?,
    val watchedAtSeconds: Long,
    val positionSeconds: Int,
    val durationSeconds: Int,
)
