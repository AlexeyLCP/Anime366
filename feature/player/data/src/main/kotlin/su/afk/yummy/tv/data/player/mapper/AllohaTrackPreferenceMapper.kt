package su.afk.yummy.tv.data.player.mapper

import su.afk.yummy.tv.core.storage.allohatrack.AllohaTrackPreferenceEntry
import su.afk.yummy.tv.domain.player.model.AllohaTrackPreference

internal fun AllohaTrackPreferenceEntry.toDomain(): AllohaTrackPreference =
    AllohaTrackPreference(
        animeId = animeId,
        dubbing = dubbing,
        player = player,
        audioLabel = audioLabel,
        subtitleLanguage = subtitleLanguage,
        subtitleLabel = subtitleLabel,
        subtitleOff = subtitleOff,
    )
