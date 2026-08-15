package su.afk.yummy.tv.data.player.extractor.alloha

import android.util.Log
import android.webkit.CookieManager
import org.json.JSONObject
import su.afk.yummy.tv.domain.player.model.PlayerStreamResolveResult

private const val LOG_TAG = "AllohaExtractor"

internal fun parseHeaders(headersJson: String): Map<String, String> {
    val objectValue = JSONObject(headersJson)
    return buildMap {
        objectValue.keys().forEach { key ->
            objectValue.optString(key).takeIf(String::isNotBlank)
                ?.let { put(key.lowercase(), it) }
        }
    }
}

internal fun parseResult(
    responseJson: String,
    headersJson: String,
    preferredQualityLabel: String?,
): PlayerStreamResolveResult.Stream {
    val headersObject = JSONObject(headersJson)
    val headers = buildMap {
        headersObject.keys().forEach { key -> put(key, headersObject.optString(key)) }
    }.filterValues(String::isNotBlank)

    val sources = JSONObject(responseJson).optJSONArray("hlsSource")
        ?: throw AllohaSourceUnavailableException("hlsSource is missing")
    Log.i(
        LOG_TAG,
        "bnsi hlsSources=${sources.length()} qualities=" +
                (0 until sources.length()).map { index ->
                    sources.optJSONObject(index)?.optJSONObject("quality")?.keys()
                        ?.asSequence()?.toList().orEmpty()
                },
    )
    val qualities = linkedMapOf<String, String>()
    for (index in 0 until sources.length()) {
        val quality = sources.optJSONObject(index)?.optJSONObject("quality") ?: continue
        quality.keys().forEach { label ->
            quality.optString(label)
                .split(" or ")
                .firstOrNull()
                ?.trim()
                ?.normalizeStreamUrl()
                ?.takeIf(String::isNotBlank)
                ?.let { qualities.putIfAbsent(label.normalizeQualityLabel(), it) }
        }
    }
    if (qualities.isEmpty()) throw AllohaSourceUnavailableException("no HLS qualities found")
    val sorted = qualities.entries
        .sortedBy { it.key.filter(Char::isDigit).toIntOrNull() ?: 0 }
        .associateTo(linkedMapOf()) { it.toPair() }
    val headersWithCookie = headers.toMutableMap().apply {
        sorted.values.firstNotNullOfOrNull { CookieManager.getInstance().getCookie(it) }
            ?.takeIf(String::isNotBlank)
            ?.let { put("Cookie", it) }
    }
    val preferredUrl = preferredQualityLabel
        ?.normalizeQualityLabel()
        ?.let(sorted::get)
    return PlayerStreamResolveResult.Stream(
        url = preferredUrl ?: sorted.values.last(),
        headers = headersWithCookie,
        qualities = sorted,
        qualityHeaders = sorted.keys.associateWith { headersWithCookie },
    )
}

/** Used both here and by [su.afk.yummy.tv.data.player.extractor.alloha.AllohaExtractor]'s bridge. */
internal fun String.normalizeStreamUrl(): String = if (startsWith("//")) "https:$this" else this

private fun String.normalizeQualityLabel(): String =
    trim().let { if (it.endsWith("p")) it else "${it}p" }
