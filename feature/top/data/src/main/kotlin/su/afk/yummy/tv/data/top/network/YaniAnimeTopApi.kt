package su.afk.yummy.tv.data.top.network

import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import su.afk.yummy.tv.core.network.anime365.Anime365SeriesListDto
import su.afk.yummy.tv.core.network.yani.YANI_BASE_URL
import su.afk.yummy.tv.core.network.yani.YaniHttpClientProvider
import su.afk.yummy.tv.data.top.dto.YaniAnimeTopItemDto
import su.afk.yummy.tv.data.top.dto.YaniAnimeTopListDto
import su.afk.yummy.tv.data.top.dto.YaniAnimeTopPosterDto
import su.afk.yummy.tv.data.top.dto.YaniAnimeTopRatingDto
import su.afk.yummy.tv.domain.top.model.AnimeTopType

class YaniAnimeTopApi(
    private val clientProvider: YaniHttpClientProvider,
) {
    suspend fun getTopAnime(type: AnimeTopType, limit: Int, offset: Int): YaniAnimeTopListDto {
        val page = clientProvider.get().get("$YANI_BASE_URL/series") {
            parameter("type", type.apiValue)
            parameter("isActive", 1)
            parameter("limit", 100)
        }.body<Anime365SeriesListDto>().data
        val ranked = page.sortedByDescending { it.myAnimeListScore ?: 0.0 }
            .drop(offset)
            .take(limit)
            .map { series ->
                YaniAnimeTopItemDto(
                    animeId = series.id,
                    title = series.displayTitle(),
                    poster = YaniAnimeTopPosterDto(
                        small = series.listPosterSmall(),
                        medium = series.listPosterSmall(),
                        big = series.listPosterFull(),
                        fullsize = series.listPosterFull(),
                    ),
                    rating = series.myAnimeListScore?.let { YaniAnimeTopRatingDto(average = it) },
                    year = series.year,
                )
            }
        return YaniAnimeTopListDto(response = ranked)
    }
}
