package su.afk.yummy.tv.feature.details.details.model

import su.afk.yummy.tv.core.model.anime.AnimeVideo
import su.afk.yummy.tv.feature.details.episodes.dubbings.EpisodeDubbingsState

data class DubbingOption(
    val video: AnimeVideo,
    val item: EpisodeDubbingsState.DubbingItem,
)
