package su.afk.yummy.tv.domain.player.model

/**
 * Ранее выбранная пользователем аудиодорожка/субтитры внутри Alloha-плеера для конкретной
 * озвучки тайтла. Ключ — (animeId, dubbing, player): набор дорожек определяется озвучкой,
 * а не серией, поэтому выбор применяется сразу ко всему тайтлу.
 */
data class AllohaTrackPreference(
    val animeId: Int,
    val dubbing: String,
    val player: String,
    val audioLabel: String?,
    val subtitleLanguage: String?,
    val subtitleLabel: String?,
    val subtitleOff: Boolean,
)
