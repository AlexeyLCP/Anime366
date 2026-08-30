package su.afk.yummy.tv.data.search.network

import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import su.afk.yummy.tv.core.network.anime365.Anime365SeriesDto
import su.afk.yummy.tv.core.network.anime365.Anime365SeriesListDto
import su.afk.yummy.tv.core.network.yani.YANI_BASE_URL
import su.afk.yummy.tv.core.network.yani.YaniHttpClientProvider
import su.afk.yummy.tv.data.search.dto.YaniSearchCatalogDto
import su.afk.yummy.tv.data.search.dto.YaniSearchGenreDto
import su.afk.yummy.tv.data.search.dto.YaniSearchGenresDto
import su.afk.yummy.tv.data.search.dto.YaniSearchItemDto
import su.afk.yummy.tv.data.search.dto.YaniSearchPosterDto
import su.afk.yummy.tv.data.search.dto.YaniSearchRatingDto
import su.afk.yummy.tv.data.search.dto.YaniSearchTypeCountDto
import su.afk.yummy.tv.data.search.dto.YaniSearchTypeDto
import su.afk.yummy.tv.domain.search.model.SearchFilters
import su.afk.yummy.tv.domain.search.model.SearchSort

class YaniSearchApi(
    private val clientProvider: YaniHttpClientProvider,
) {
    suspend fun search(query: String, filters: SearchFilters, limit: Int, offset: Int): List<YaniSearchItemDto> =
        fetchSeries(query, filters, limit, offset)
            .sortedWith(filters.seriesComparator())
            .map { it.toSearchItem() }

    suspend fun getRandomAnime(): List<YaniSearchItemDto> =
        fetchSeries("", SearchFilters.EMPTY, limit = 50, offset = 0)
            .randomOrNull()
            ?.let { listOf(it.toSearchItem()) }
            .orEmpty()

    suspend fun getGenres(): YaniSearchGenresDto = YaniSearchGenresDto(
        genres = GENRES.map { (id, title) -> YaniSearchGenreDto(title = title, value = id) },
    )

    suspend fun getCatalog(): YaniSearchCatalogDto = YaniSearchCatalogDto(
        types = CATALOG_TYPES.map { (alias, name) ->
            YaniSearchTypeCountDto(type = YaniSearchTypeDto(name = name, alias = alias))
        },
    )

    private suspend fun fetchSeries(
        query: String,
        filters: SearchFilters,
        limit: Int,
        offset: Int,
    ): List<Anime365SeriesDto> =
        clientProvider.get().get("$YANI_BASE_URL/series") {
            parameter("limit", limit)
            parameter("offset", offset)
            parameter("isActive", 1)
            query.takeIf { it.isNotBlank() }?.let { parameter("query", it) }
            filters.genres.takeIf { it.isNotEmpty() }?.let { ids ->
                parameter("chips", "genre@=${ids.joinToString(",")}")
            }
            filters.types.singleOrNull()?.let { parameter("type", it) }
            when {
                "ongoing" in filters.statuses && "released" !in filters.statuses -> parameter("isAiring", 1)
                "released" in filters.statuses && "ongoing" !in filters.statuses -> parameter("isAiring", 0)
            }
            val year = filters.fromYear?.takeIf { it == filters.toYear || filters.toYear == null }
            year?.let { parameter("year", it) }
            if (filters.sort == SearchSort.ID) parameter("order", "id")
        }.body<Anime365SeriesListDto>().data
}

private fun SearchFilters.seriesComparator(): Comparator<Anime365SeriesDto> {
    val base = when (sort) {
        SearchSort.TITLE -> compareBy<Anime365SeriesDto> { it.displayTitle().lowercase() }
        SearchSort.YEAR -> compareBy { it.year ?: 0 }
        SearchSort.RATING, SearchSort.TOP, SearchSort.RATING_COUNTERS, SearchSort.VIEWS ->
            compareBy { it.myAnimeListScore ?: 0.0 }
        SearchSort.ID -> compareBy { it.id }
        SearchSort.RELEVANCE -> compareBy { 0 }
    }
    return if (sortForward) base else base.reversed()
}

private fun Anime365SeriesDto.toSearchItem(): YaniSearchItemDto = YaniSearchItemDto(
    animeId = id,
    title = displayTitle(),
    poster = YaniSearchPosterDto(
        small = posterUrlSmall,
        medium = posterUrl,
        big = posterUrl,
        fullsize = posterUrl,
    ),
    rating = myAnimeListScore?.let { YaniSearchRatingDto(average = it) },
    year = year,
)

private val CATALOG_TYPES = listOf(
    "tv" to "ТВ сериал",
    "movie" to "Фильм",
    "ova" to "OVA",
    "ona" to "ONA",
    "special" to "Спешл",
    "music" to "Клип",
)

private val GENRES = listOf(
    1 to "Экшен",
    2 to "Приключения",
    4 to "Комедия",
    8 to "Драма",
    10 to "Фэнтези",
    14 to "Ужасы",
    22 to "Романтика",
    24 to "Фантастика",
    27 to "Сёнен",
    36 to "Сёдзё",
    37 to "Повседневность",
    40 to "Психологическое",
    41 to "Триллер",
    46 to "Удостоено наград",
)
