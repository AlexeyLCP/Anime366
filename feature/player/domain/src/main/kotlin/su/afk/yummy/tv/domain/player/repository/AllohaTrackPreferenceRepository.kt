package su.afk.yummy.tv.domain.player.repository

import su.afk.yummy.tv.domain.player.model.AllohaTrackPreference

/** Читает/пишет запомненный выбор аудиодорожки и субтитров Alloha-плеера для озвучки тайтла. */
interface AllohaTrackPreferenceRepository {

    suspend fun get(animeId: Int, dubbing: String, player: String): AllohaTrackPreference?

    suspend fun save(preference: AllohaTrackPreference)
}
