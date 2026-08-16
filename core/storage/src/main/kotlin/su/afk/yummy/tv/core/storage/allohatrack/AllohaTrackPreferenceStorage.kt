package su.afk.yummy.tv.core.storage.allohatrack

/** Абстракция над локальным хранилищем выбора Alloha-дорожек — позволяет подменять реализацию в тестах. */
interface AllohaTrackPreferenceStorage {

    suspend fun get(animeId: Int, dubbing: String, player: String): AllohaTrackPreferenceEntry?

    suspend fun save(
        animeId: Int,
        dubbing: String,
        player: String,
        audioLabel: String?,
        subtitleLanguage: String?,
        subtitleLabel: String?,
        subtitleOff: Boolean,
        updatedAt: Long = System.currentTimeMillis(),
    )
}
