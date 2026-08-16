package su.afk.yummy.tv.core.storage.allohatrack

import androidx.room.Entity

/**
 * Запоминает выбор пользователя внутри Alloha-плеера (альтернативная аудиодорожка/субтитры)
 * для конкретной озвучки тайтла — набор дорожек определяется озвучкой, а не серией, поэтому
 * ключ не включает эпизод.
 */
@Entity(
    tableName = "alloha_track_preference",
    primaryKeys = ["animeId", "dubbing", "player"],
)
data class AllohaTrackPreferenceEntry(
    val animeId: Int,
    val dubbing: String,
    val player: String,
    val audioLabel: String? = null,
    val subtitleLanguage: String? = null,
    val subtitleLabel: String? = null,
    val subtitleOff: Boolean = false,
    val updatedAt: Long = System.currentTimeMillis(),
)
