package su.afk.yummy.tv.feature.player.model

data class PlayerProgressSnapshot(
    val episode: String,
    val episodeUrl: String,
    val videoId: Int,
    val playerName: String,
    val dubbing: String,
    val screenshotUrl: String,
    val positionMs: Long,
    val durationMs: Long,
)
