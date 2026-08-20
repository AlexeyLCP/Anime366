package su.afk.yummy.tv.feature.details.episodes.dubbings

import su.afk.yummy.tv.core.model.anime.AnimeVideo
import su.afk.yummy.tv.core.model.settings.PreferredPlayer
import su.afk.yummy.tv.core.utils.episode.episodeGroupKey
import su.afk.yummy.tv.feature.details.utils.matchesPreferredPlayer
import su.afk.yummy.tv.feature.player.isSupportedPlayerUrl
import su.afk.yummy.tv.feature.player.playerDisplayOrderPriority

internal fun List<AnimeVideo>.selectEpisodeDubbingLaunchVideo(
    episode: String,
    dubbingName: String,
    preferredPlayer: PreferredPlayer,
): AnimeVideo? {
    val candidates = filter {
        it.episode.episodeGroupKey() == episode.episodeGroupKey() && it.dubbing.trim() == dubbingName
    }
    val supported = candidates.filter { it.iframeUrl.isSupportedPlayerUrl() }
    return supported.firstOrNull { it.iframeUrl.matchesPreferredPlayer(preferredPlayer) }
        ?: supported.minWithOrNull(
            compareBy<AnimeVideo> {
                minOf(
                    it.player.playerDisplayOrderPriority(),
                    it.iframeUrl.playerDisplayOrderPriority(),
                )
            }.thenBy { it.player }
        )
        ?: candidates.firstOrNull()
}
