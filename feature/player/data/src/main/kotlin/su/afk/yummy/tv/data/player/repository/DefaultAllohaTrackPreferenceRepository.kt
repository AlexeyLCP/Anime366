package su.afk.yummy.tv.data.player.repository

import su.afk.yummy.tv.core.storage.allohatrack.AllohaTrackPreferenceStorage
import su.afk.yummy.tv.data.player.mapper.toDomain
import su.afk.yummy.tv.domain.player.model.AllohaTrackPreference
import su.afk.yummy.tv.domain.player.repository.AllohaTrackPreferenceRepository
import javax.inject.Inject

internal class DefaultAllohaTrackPreferenceRepository @Inject constructor(
    private val store: AllohaTrackPreferenceStorage,
) : AllohaTrackPreferenceRepository {

    override suspend fun get(
        animeId: Int,
        dubbing: String,
        player: String,
    ): AllohaTrackPreference? = store.get(animeId, dubbing, player)?.toDomain()

    override suspend fun save(preference: AllohaTrackPreference) {
        store.save(
            animeId = preference.animeId,
            dubbing = preference.dubbing,
            player = preference.player,
            audioLabel = preference.audioLabel,
            subtitleLanguage = preference.subtitleLanguage,
            subtitleLabel = preference.subtitleLabel,
            subtitleOff = preference.subtitleOff,
        )
    }
}
