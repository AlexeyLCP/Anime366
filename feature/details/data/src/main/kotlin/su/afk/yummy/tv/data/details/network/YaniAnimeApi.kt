package su.afk.yummy.tv.data.details.network

import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import su.afk.yummy.tv.core.network.anime365.ANIME365_PLAYER_NAME
import su.afk.yummy.tv.core.network.anime365.Anime365SeriesDto
import su.afk.yummy.tv.core.network.anime365.Anime365SeriesItemDto
import su.afk.yummy.tv.core.network.anime365.Anime365SeriesListDto
import su.afk.yummy.tv.core.network.anime365.Anime365TranslationListDto
import su.afk.yummy.tv.core.network.anime365.dubbingLabel
import su.afk.yummy.tv.core.network.anime365.embedPageUrl
import su.afk.yummy.tv.core.network.anime365.seasonSearchQuery
import su.afk.yummy.tv.core.network.yani.YANI_BASE_URL
import su.afk.yummy.tv.core.network.yani.YaniHttpClientProvider
import su.afk.yummy.tv.data.details.dto.YaniAnimeDetailsDto
import su.afk.yummy.tv.data.details.dto.YaniAnimePosterDto
import su.afk.yummy.tv.data.details.dto.YaniAnimeRatingDto
import su.afk.yummy.tv.data.details.dto.YaniAnimeResponseDto
import su.afk.yummy.tv.data.details.dto.YaniAnimeTypeDto
import su.afk.yummy.tv.data.details.dto.YaniAnimeVideoDto
import su.afk.yummy.tv.data.details.dto.YaniAnimeVideosDto
import su.afk.yummy.tv.data.details.dto.YaniDirectorDetailsDto
import su.afk.yummy.tv.data.details.dto.YaniDirectorResponseDto
import su.afk.yummy.tv.data.details.dto.YaniEpisodesDto
import su.afk.yummy.tv.data.details.dto.YaniGenreDetailsDto
import su.afk.yummy.tv.data.details.dto.YaniGenreResponseDto
import su.afk.yummy.tv.data.details.dto.YaniNamedDto
import su.afk.yummy.tv.data.details.dto.YaniRecommendationMutationResponseDto
import su.afk.yummy.tv.data.details.dto.YaniRecommendationVoteResponseDto
import su.afk.yummy.tv.data.details.dto.YaniRecommendationsDto
import su.afk.yummy.tv.data.details.dto.YaniRelatedAnimeDto
import su.afk.yummy.tv.data.details.dto.YaniRelatedAnimeResponseDto
import su.afk.yummy.tv.data.details.dto.YaniRemoteIdsDto
import su.afk.yummy.tv.data.details.dto.YaniStudioDetailsDto
import su.afk.yummy.tv.data.details.dto.YaniStudioResponseDto
import su.afk.yummy.tv.data.details.dto.YaniTrailersResponseDto
import su.afk.yummy.tv.data.details.dto.YaniVideoDataDto
import su.afk.yummy.tv.data.details.dto.YaniViewingOrderDataDto
import su.afk.yummy.tv.data.details.dto.YaniViewingOrderItemDto
import su.afk.yummy.tv.domain.anime.model.AnimeRelationKind

class YaniAnimeApi(
    private val clientProvider: YaniHttpClientProvider,
) {
    suspend fun getAnimeDetails(animeId: Int): YaniAnimeDetailsDto {
        val series = clientProvider.get().get("$YANI_BASE_URL/series/$animeId")
            .body<Anime365SeriesItemDto>().data ?: return YaniAnimeDetailsDto()
        return YaniAnimeDetailsDto(response = series.toDetails(fetchSeasons(series)))
    }

    suspend fun getAnimeVideos(animeId: Int): YaniAnimeVideosDto {
        val translations = clientProvider.get().get("$YANI_BASE_URL/translations") {
            parameter("seriesId", animeId)
            parameter("limit", 1000)
            parameter("isActive", 1)
        }.body<Anime365TranslationListDto>().data
        return YaniAnimeVideosDto(
            response = translations.map { translation ->
                YaniAnimeVideoDto(
                    videoId = translation.id,
                    data = YaniVideoDataDto(
                        player = ANIME365_PLAYER_NAME,
                        dubbing = translation.dubbingLabel(),
                    ),
                    number = translation.episode?.numberLabel().orEmpty(),
                    iframeUrl = translation.embedPageUrl(),
                    duration = translation.duration?.toInt(),
                )
            },
        )
    }

    suspend fun getAnimeRecommendations(animeId: Int, fromAi: Boolean): YaniRecommendationsDto =
        YaniRecommendationsDto()

    suspend fun getAnimeTrailers(animeId: Int): YaniTrailersResponseDto = YaniTrailersResponseDto()

    suspend fun getStudio(url: String): YaniStudioDetailsDto =
        YaniStudioDetailsDto(response = YaniStudioResponseDto(title = url, url = url))

    suspend fun getDirector(id: Int): YaniDirectorDetailsDto =
        YaniDirectorDetailsDto(response = YaniDirectorResponseDto(id = id))

    suspend fun getGenre(id: Int): YaniGenreDetailsDto =
        YaniGenreDetailsDto(response = YaniGenreResponseDto(id = id, title = "Жанр $id"))

    suspend fun getRelatedAnime(kind: AnimeRelationKind, id: Int): YaniRelatedAnimeResponseDto {
        if (kind != AnimeRelationKind.GENRE) return YaniRelatedAnimeResponseDto()
        val series = clientProvider.get().get("$YANI_BASE_URL/series") {
            parameter("chips", "genre@=$id")
            parameter("limit", 100)
            parameter("isActive", 1)
        }.body<Anime365SeriesListDto>().data
        return YaniRelatedAnimeResponseDto(response = series.map { it.toRelated() })
    }

    suspend fun ignoreAnimeRecommendation(animeId: Int): YaniRecommendationMutationResponseDto =
        YaniRecommendationMutationResponseDto(response = true)

    suspend fun restoreAnimeRecommendation(animeId: Int): YaniRecommendationMutationResponseDto =
        YaniRecommendationMutationResponseDto(response = true)

    suspend fun voteAnimeRecommendation(
        animeId: Int,
        similarAnimeId: Int,
        action: Int,
    ): YaniRecommendationVoteResponseDto = YaniRecommendationVoteResponseDto()

    suspend fun deleteAnimeRecommendationVote(
        animeId: Int,
        similarAnimeId: Int,
    ): YaniRecommendationVoteResponseDto = YaniRecommendationVoteResponseDto()

    private suspend fun fetchSeasons(series: Anime365SeriesDto): List<Anime365SeriesDto> {
        val query = series.seasonSearchQuery()
        if (query.length < 2) return listOf(series)
        val found = runCatching {
            clientProvider.get().get("$YANI_BASE_URL/series") {
                parameter("query", query)
                parameter("limit", 24)
                parameter("isActive", 1)
            }.body<Anime365SeriesListDto>().data
        }.getOrDefault(emptyList())
        return (listOf(series) + found.filter { it.id != series.id })
            .distinctBy { it.id }
            .sortedWith(compareBy<Anime365SeriesDto> { it.year ?: Int.MAX_VALUE }.thenBy { it.id })
    }
}

private fun Anime365SeriesDto.toDetails(
    seasons: List<Anime365SeriesDto>,
): YaniAnimeResponseDto {
    val otherTitles = listOfNotNull(titles?.romaji, titles?.en, titles?.ja)
        .filter { it.isNotBlank() && it != displayTitle() }
    return YaniAnimeResponseDto(
        animeId = id,
        animeUrl = url.orEmpty(),
        title = displayTitle(),
        description = description(),
        poster = YaniAnimePosterDto(
            small = posterUrlSmall,
            medium = posterUrl,
            big = posterUrl,
            fullsize = posterUrl,
        ),
        rating = YaniAnimeRatingDto(average = myAnimeListScore, myAnimeList = myAnimeListScore),
        genres = genres.map { YaniNamedDto(id = it.id, title = it.title, url = it.url) },
        year = year,
        animeStatus = null,
        type = YaniAnimeTypeDto(name = typeTitle, shortname = type),
        episodes = YaniEpisodesDto(
            count = numberOfEpisodes,
            aired = episodes.count { it.isActive == 1 && it.episodeType != "preview" },
        ),
        otherTitles = otherTitles,
        viewingOrder = seasons.map { it.toViewingOrderItem() },
        remoteIds = YaniRemoteIdsDto(myAnimeListId = myAnimeListId),
    )
}

private fun Anime365SeriesDto.toViewingOrderItem(): YaniViewingOrderItemDto =
    YaniViewingOrderItemDto(
        animeId = id,
        title = displayTitle(),
        data = YaniViewingOrderDataDto(text = typeTitle ?: type),
        type = YaniAnimeTypeDto(name = typeTitle, shortname = type),
        poster = YaniAnimePosterDto(
            small = posterUrlSmall,
            medium = posterUrl,
            big = posterUrl,
            fullsize = posterUrl,
        ),
        year = year,
        rating = myAnimeListScore,
    )

private fun Anime365SeriesDto.toRelated(): YaniRelatedAnimeDto = YaniRelatedAnimeDto(
    animeId = id,
    title = displayTitle(),
    poster = YaniAnimePosterDto(
        small = posterUrlSmall,
        medium = posterUrl,
        big = posterUrl,
        fullsize = posterUrl,
    ),
    rating = YaniAnimeRatingDto(average = myAnimeListScore, myAnimeList = myAnimeListScore),
    year = year,
)
