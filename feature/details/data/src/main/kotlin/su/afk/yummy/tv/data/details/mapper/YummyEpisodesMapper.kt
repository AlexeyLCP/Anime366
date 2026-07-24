package su.afk.yummy.tv.data.details.mapper

import su.afk.yummy.tv.core.model.anime.AnimeEpisodeInfo
import su.afk.yummy.tv.data.details.dto.YummyEpisodesDto

/** Ключ — номер серии строкой, как в [su.afk.yummy.tv.core.model.anime.AnimeVideo.episode]. */
internal fun YummyEpisodesDto.toAnimeEpisodeInfoByNumber(): Map<String, AnimeEpisodeInfo> =
    episodes.asSequence()
        .filter { it.episodeNumber > 0 }
        .map { episode ->
            episode.episodeNumber.toString() to AnimeEpisodeInfo(
                number = episode.episodeNumber,
                title = episode.title?.trim()?.takeIf { it.isNotEmpty() },
                description = episode.overview?.trim()?.takeIf { it.isNotEmpty() },
            )
        }
        .filter { (_, info) -> info.title != null || info.description != null }
        .toMap()
