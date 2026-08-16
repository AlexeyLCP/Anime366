package su.afk.yummy.tv.core.storage.allohatrack

internal class AllohaTrackPreferenceStore(
    private val dao: AllohaTrackPreferenceDao,
) : AllohaTrackPreferenceStorage {

    override suspend fun get(
        animeId: Int,
        dubbing: String,
        player: String,
    ): AllohaTrackPreferenceEntry? = dao.get(animeId, dubbing, player)

    override suspend fun save(
        animeId: Int,
        dubbing: String,
        player: String,
        audioLabel: String?,
        subtitleLanguage: String?,
        subtitleLabel: String?,
        subtitleOff: Boolean,
        updatedAt: Long,
    ) {
        dao.save(
            AllohaTrackPreferenceEntry(
                animeId = animeId,
                dubbing = dubbing,
                player = player,
                audioLabel = audioLabel,
                subtitleLanguage = subtitleLanguage,
                subtitleLabel = subtitleLabel,
                subtitleOff = subtitleOff,
                updatedAt = updatedAt,
            )
        )
    }
}
