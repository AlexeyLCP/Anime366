package su.afk.yummy.tv.data.schedule.network

import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import su.afk.yummy.tv.core.network.anime365.Anime365SeriesListDto
import su.afk.yummy.tv.core.network.yani.YANI_BASE_URL
import su.afk.yummy.tv.core.network.yani.YaniHttpClientProvider
import su.afk.yummy.tv.data.schedule.dto.YaniScheduleAnimeDto
import su.afk.yummy.tv.data.schedule.dto.YaniScheduleEpisodesDto
import su.afk.yummy.tv.data.schedule.dto.YaniSchedulePosterDto
import su.afk.yummy.tv.data.schedule.dto.YaniScheduleResponseDto

class YaniScheduleApi(
    private val clientProvider: YaniHttpClientProvider,
) {
    suspend fun getSchedule(): YaniScheduleResponseDto {
        val series = clientProvider.get().get("$YANI_BASE_URL/series") {
            parameter("isAiring", 1)
            parameter("isActive", 1)
            parameter("limit", 50)
        }.body<Anime365SeriesListDto>().data
        return YaniScheduleResponseDto(
            response = series.map {
                YaniScheduleAnimeDto(
                    animeId = it.id,
                    title = it.displayTitle(),
                    poster = YaniSchedulePosterDto(
                        small = it.posterUrlSmall,
                        medium = it.posterUrl,
                        big = it.posterUrl,
                        fullsize = it.posterUrl,
                    ),
                    episodes = YaniScheduleEpisodesDto(count = it.numberOfEpisodes),
                )
            },
        )
    }
}
