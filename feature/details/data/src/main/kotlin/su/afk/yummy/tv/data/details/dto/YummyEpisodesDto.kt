package su.afk.yummy.tv.data.details.dto

import kotlinx.serialization.Serializable

/**
 * Ответ YummyTV API по серии сезона: /api/anime/mal/{malId}.
 * Превью не используем — миниатюры берём из Kodik.
 */
@Serializable
data class YummyEpisodesDto(
    val malId: Int? = null,
    val tmdbId: Int? = null,
    val seasonNumber: Int? = null,
    val episodes: List<YummyEpisodeDto> = emptyList(),
)

@Serializable
data class YummyEpisodeDto(
    val episodeNumber: Int = 0,
    val title: String? = null,
    val overview: String? = null,
)
