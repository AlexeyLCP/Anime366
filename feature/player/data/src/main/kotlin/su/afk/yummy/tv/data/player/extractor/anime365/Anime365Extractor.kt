package su.afk.yummy.tv.data.player.extractor.anime365

import android.content.Context
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import su.afk.yummy.tv.core.network.anime365.ANIME365_PLAYER_NAME
import su.afk.yummy.tv.core.network.anime365.Anime365EmbedDto
import su.afk.yummy.tv.core.network.yani.ANIME365_HOSTS
import su.afk.yummy.tv.core.network.yani.ANIME365_USER_AGENT
import su.afk.yummy.tv.core.network.yani.YANI_BASE_URL
import su.afk.yummy.tv.core.network.yani.YaniApiJson
import su.afk.yummy.tv.core.network.yani.YaniHttpClientProvider
import su.afk.yummy.tv.data.player.extractor.PlayerStreamExtractor
import su.afk.yummy.tv.domain.player.model.AllohaSubtitleTrack
import su.afk.yummy.tv.domain.player.model.PlayerStreamRequest
import su.afk.yummy.tv.domain.player.model.PlayerStreamResolveResult
import javax.inject.Inject

internal class Anime365Extractor @Inject constructor(
    private val clientProvider: YaniHttpClientProvider,
) : PlayerStreamExtractor {

    override fun supports(url: String): Boolean {
        val id = translationId(url) ?: return false
        val lower = url.lowercase()
        return id > 0 && (
            ANIME365_HOSTS.any { it in lower } ||
                lower.startsWith("anime365:") ||
                ANIME365_PLAYER_NAME.lowercase() in lower
            )
    }

    override suspend fun extract(
        request: PlayerStreamRequest,
        context: Context,
    ): PlayerStreamResolveResult {
        val id = translationId(request.iframeUrl) ?: return PlayerStreamResolveResult.Failed
        val body = clientProvider.get().get("$YANI_BASE_URL/translations/embed/$id").bodyAsText()
        val data = runCatching {
            YaniApiJson.decodeFromString(Anime365EmbedDto.serializer(), body).data
        }.getOrNull() ?: return PlayerStreamResolveResult.Failed
        val qualities = LinkedHashMap<String, String>()
        data.stream.sortedBy { it.height }.forEach { stream ->
            val url = stream.urls.firstOrNull().orEmpty()
            if (stream.height > 0 && url.isNotBlank()) {
                qualities["${stream.height}"] = url
            }
        }
        if (qualities.isEmpty()) {
            data.download.sortedBy { it.height }.forEach { item ->
                if (item.height > 0 && item.url.isNotBlank()) qualities["${item.height}"] = item.url
            }
        }
        val url = qualities[request.autoQualityLabel.filter { it.isDigit() }]
            ?: qualities.values.lastOrNull()
            ?: return PlayerStreamResolveResult.Unavailable("Нет видео")
        val subtitles = data.subtitlesUrl?.takeIf { it.isNotBlank() }?.let { subUrl ->
            val format = subUrl.substringAfterLast('.', "ass").substringBefore('?').lowercase()
            listOf(
                AllohaSubtitleTrack(
                    label = "Субтитры",
                    url = subUrl,
                    language = "ru",
                    format = format,
                ),
            )
        }.orEmpty()
        return PlayerStreamResolveResult.Stream(
            url = url,
            qualities = qualities.takeIf { it.size > 1 },
            headers = STREAM_HEADERS,
            allohaSubtitles = subtitles,
        )
    }

    private companion object {
        val TRANSLATION_ID = Regex("""(?:embed/|anime365:)(\d+)""")
        val STREAM_HEADERS = mapOf(
            "User-Agent" to ANIME365_USER_AGENT,
            "Referer" to "https://anime-365.ru/",
        )

        fun translationId(url: String): Int? =
            TRANSLATION_ID.find(url)?.groupValues?.get(1)?.toIntOrNull()
    }
}
