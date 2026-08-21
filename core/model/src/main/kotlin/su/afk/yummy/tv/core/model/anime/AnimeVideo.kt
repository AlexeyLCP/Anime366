package su.afk.yummy.tv.core.model.anime

data class AnimeVideo(
    val id: Int,
    val episode: String,
    val dubbing: String,
    val player: String,
    val playerId: Int?,
    val iframeUrl: String,
    val durationSeconds: Int?,
    val views: Int? = null,
    val watchedEndTimeSeconds: Int? = null,
    val watchedDateSeconds: Long? = null,
    /** Подписка на новые серии этой озвучки и балансера — состояние приходит от сервера. */
    val isSubscribed: Boolean = false,
    val skips: AnimeVideoSkips = AnimeVideoSkips(),
)
