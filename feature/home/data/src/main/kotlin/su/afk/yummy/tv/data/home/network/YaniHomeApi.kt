package su.afk.yummy.tv.data.home.network

import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import su.afk.yummy.tv.core.network.anime365.Anime365SeriesDto
import su.afk.yummy.tv.core.network.anime365.Anime365SeriesListDto
import su.afk.yummy.tv.core.network.anime365.Anime365TranslationDto
import su.afk.yummy.tv.core.network.anime365.Anime365TranslationListDto
import su.afk.yummy.tv.core.network.yani.YANI_BASE_URL
import su.afk.yummy.tv.core.network.yani.YaniHttpClientProvider
import su.afk.yummy.tv.data.home.dto.YaniAnimeDto
import su.afk.yummy.tv.data.home.dto.YaniCarouselDto
import su.afk.yummy.tv.data.home.dto.YaniFeedDto
import su.afk.yummy.tv.data.home.dto.YaniFeedResponseDto
import su.afk.yummy.tv.data.home.dto.YaniPosterDto
import su.afk.yummy.tv.data.home.dto.YaniRatingDto
import su.afk.yummy.tv.data.home.dto.YaniVideoDto
import java.util.Calendar

class YaniHomeApi(
    private val clientProvider: YaniHttpClientProvider,
) {
    suspend fun getFeed(): YaniFeedDto = coroutineScope {
        val airingDeferred = async { series(isAiring = 1, limit = FEED_LIMIT) }
        val recentDeferred = async { translations(limit = FEED_LIMIT) }
        val freshDeferred = async {
            series(year = Calendar.getInstance().get(Calendar.YEAR), limit = FEED_LIMIT)
        }
        val airing = airingDeferred.await()
        val recent = recentDeferred.await()
        val fresh = freshDeferred.await()
        YaniFeedDto(
            response = YaniFeedResponseDto(
                announcements = airing.take(8).map { it.toYaniAnime() },
                topCarousel = YaniCarouselDto(items = airing.take(8).map { it.toYaniAnime() }),
                new = fresh.map { it.toYaniAnime() },
                recommends = airing.map { it.toYaniAnime() },
                newVideos = recent.map { it.toYaniVideo() },
                schedule = airing.map { it.toYaniAnime() },
            ),
        )
    }

    private suspend fun series(isAiring: Int? = null, year: Int? = null, limit: Int): List<Anime365SeriesDto> =
        clientProvider.get().get("$YANI_BASE_URL/series") {
            parameter("limit", limit)
            parameter("isActive", 1)
            isAiring?.let { parameter("isAiring", it) }
            year?.let { parameter("year", it) }
        }.body<Anime365SeriesListDto>().data

    private suspend fun translations(limit: Int): List<Anime365TranslationDto> =
        clientProvider.get().get("$YANI_BASE_URL/translations") {
            parameter("feed", "recent")
            parameter("limit", limit)
            parameter("isActive", 1)
        }.body<Anime365TranslationListDto>().data
}

private const val FEED_LIMIT = 24

private fun Anime365SeriesDto.toYaniAnime(): YaniAnimeDto = YaniAnimeDto(
    animeId = id,
    animeUrl = url,
    title = displayTitle(),
    description = description(),
    poster = YaniPosterDto(
        small = listPosterSmall(),
        medium = listPosterSmall(),
        big = listPosterFull(),
        fullsize = listPosterFull(),
        mega = listPosterFull(),
    ),
    rating = myAnimeListScore?.let { YaniRatingDto(average = it) },
    year = year,
)

private fun Anime365TranslationDto.toYaniVideo(): YaniVideoDto {
    val seriesDto = series
    return YaniVideoDto(
        videoId = id,
        animeId = seriesId,
        animeUrl = seriesDto?.url,
        title = seriesDto?.displayTitle()?.ifBlank { title } ?: title,
        episodeTitle = episode?.episodeFull?.ifBlank { null },
        dubTitle = authorsSummary.ifBlank { null },
        playerTitle = qualityType.ifBlank { "Anime365" },
        poster = YaniPosterDto(
            small = seriesDto?.listPosterSmall(),
            medium = seriesDto?.listPosterSmall(),
            big = seriesDto?.listPosterFull(),
            fullsize = seriesDto?.listPosterFull(),
            mega = seriesDto?.listPosterFull(),
        ),
    )
}
