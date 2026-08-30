package su.afk.yummy.tv.core.network.anime365

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Anime365SeriesListDto(val data: List<Anime365SeriesDto> = emptyList())

@Serializable
data class Anime365SeriesItemDto(val data: Anime365SeriesDto? = null)

@Serializable
data class Anime365SeriesDto(
    val id: Int = 0,
    val myAnimeListId: Int? = null,
    val myAnimeListScore: Double? = null,
    val numberOfEpisodes: Int? = null,
    val season: String? = null,
    val year: Int? = null,
    val type: String? = null,
    val typeTitle: String? = null,
    val isActive: Int = 1,
    val isAiring: Int = 0,
    val posterUrl: String? = null,
    val posterUrlSmall: String? = null,
    val titles: Anime365TitlesDto? = null,
    val title: String? = null,
    val url: String? = null,
    val descriptions: List<Anime365DescriptionDto> = emptyList(),
    val genres: List<Anime365GenreDto> = emptyList(),
    val episodes: List<Anime365EpisodeDto> = emptyList(),
) {
    fun displayTitle(): String =
        titles?.ru?.takeIf { it.isNotBlank() }
            ?: titles?.romaji?.takeIf { it.isNotBlank() }
            ?: title.orEmpty()

    fun description(): String =
        descriptions.firstOrNull { it.source.contains("shikimori", ignoreCase = true) }?.value
            ?: descriptions.firstOrNull()?.value.orEmpty()
}

@Serializable
data class Anime365TitlesDto(
    val ru: String? = null,
    val romaji: String? = null,
    val en: String? = null,
    val ja: String? = null,
)

@Serializable
data class Anime365DescriptionDto(
    val source: String = "",
    val value: String = "",
)

@Serializable
data class Anime365GenreDto(
    val id: Int = 0,
    val title: String = "",
    val url: String? = null,
)

@Serializable
data class Anime365EpisodeDto(
    val id: Int = 0,
    val seriesId: Int = 0,
    val episodeFull: String = "",
    val episodeInt: Double? = null,
    val episodeTitle: String = "",
    val episodeType: String = "",
    val isActive: Int = 1,
) {
    fun numberLabel(): String {
        val n = episodeInt ?: return episodeFull
        return if (n % 1.0 == 0.0) n.toInt().toString() else n.toString()
    }
}

@Serializable
data class Anime365TranslationListDto(val data: List<Anime365TranslationDto> = emptyList())

@Serializable
data class Anime365TranslationDto(
    val id: Int = 0,
    val seriesId: Int = 0,
    val episodeId: Int = 0,
    val authorsSummary: String = "",
    val qualityType: String = "",
    val type: String = "",
    val typeKind: String = "",
    val typeLang: String = "",
    val title: String = "",
    val embedUrl: String? = null,
    val duration: Double? = null,
    val isActive: Int = 1,
    val episode: Anime365EpisodeDto? = null,
    val series: Anime365SeriesDto? = null,
)

@Serializable
data class Anime365UserItemDto(val data: Anime365UserDto? = null)

@Serializable
data class Anime365UserDto(
    val isLogined: Boolean = false,
    val id: Int = 0,
    val name: String = "",
    val isPremium: Boolean = false,
    val premiumUntil: String? = null,
)

@Serializable
data class Anime365AccessTokenDto(val data: Anime365AccessTokenPayloadDto? = null)

@Serializable
data class Anime365AccessTokenPayloadDto(
    @SerialName("access_token") val accessToken: String = "",
)

@Serializable
data class Anime365EmbedDto(val data: Anime365EmbedDataDto? = null)

@Serializable
data class Anime365EmbedDataDto(
    val embedUrl: String? = null,
    val download: List<Anime365HeightUrlDto> = emptyList(),
    val stream: List<Anime365StreamDto> = emptyList(),
    val subtitlesUrl: String? = null,
)

@Serializable
data class Anime365HeightUrlDto(
    val height: Int = 0,
    val url: String = "",
)

@Serializable
data class Anime365StreamDto(
    val height: Int = 0,
    val urls: List<String> = emptyList(),
)

@Serializable
data class Anime365ErrorDto(val error: Anime365ErrorBodyDto? = null)

@Serializable
data class Anime365ErrorBodyDto(
    val code: Int = 0,
    val message: String = "",
)

fun Anime365TranslationDto.embedPageUrl(): String =
    embedUrl?.takeIf { it.isNotBlank() } ?: "$ANIME365_EMBED_PREFIX$id"

fun Anime365TranslationDto.kindLabel(): String = when {
    typeKind.equals("sub", ignoreCase = true) || type.startsWith("sub", ignoreCase = true) ->
        "Субтитры"
    typeKind.equals("raw", ignoreCase = true) || type.equals("raw", ignoreCase = true) ->
        "Оригинал"
    else -> "Озвучка"
}

fun Anime365TranslationDto.dubbingLabel(): String {
    val kind = kindLabel()
    val author = authorsSummary.ifBlank { title }.ifBlank { kind }
    val quality = qualityType.uppercase()
    return buildString {
        append(kind)
        if (author != kind) append(" · ").append(author)
        if (quality.isNotBlank() && quality != "TV") append(" ($quality)")
        if (typeLang.isNotBlank() && !typeLang.equals("ru", ignoreCase = true)) {
            append(" · ").append(typeLang.uppercase())
        }
    }
}

fun Anime365SeriesDto.seasonSearchQuery(): String {
    val raw = titles?.romaji?.ifBlank { null }
        ?: titles?.en?.ifBlank { null }
        ?: titles?.ru?.ifBlank { null }
        ?: title.orEmpty()
    val stripped = raw
        .replace(
            Regex("""(?i)\s*[:.\-]?\s*(\d+(st|nd|rd|th)?\s*)?(season|сезон|cour|part).*$"""),
            "",
        )
        .replace(Regex("""\s+\d+$"""), "")
        .trim()
    return stripped.ifBlank { raw.trim() }
}

const val ANIME365_EMBED_PREFIX = "https://anime-365.ru/translations/embed/"
const val ANIME365_PLAYER_NAME = "Anime365"
