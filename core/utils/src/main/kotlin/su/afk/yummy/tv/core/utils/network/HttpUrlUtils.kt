package su.afk.yummy.tv.core.utils.network

import java.net.URI

fun String.toHttpsUrl(): String = when {
    startsWith("//") -> "https:$this"
    startsWith("http://") -> replaceFirst("http://", "https://")
    else -> this
}

/** Приводит nullable-URL к https, обрезая пробелы; пустые/бланковые значения → null. */
fun String?.toHttpsUrlOrNull(): String? =
    this?.trim()?.takeIf { it.isNotEmpty() }?.toHttpsUrl()

fun String.normalizedHttpUrl(): String =
    when {
        startsWith("//") -> "https:$this"
        startsWith("http://") || startsWith("https://") -> this
        isNotBlank() -> "https://$this"
        else -> this
    }

fun String.httpOriginOrNull(): String? =
    runCatching {
        val uri = URI(this)
        val scheme = uri.scheme ?: return@runCatching null
        val host = uri.host ?: return@runCatching null
        val port = uri.port.takeIf { it > 0 }?.let { ":$it" }.orEmpty()
        "$scheme://$host$port"
    }.getOrNull()

fun String.isLikelyImageUrl(): Boolean =
    Regex("""\.(webp|avif|jpe?g|png)(\?.*)?$""", RegexOption.IGNORE_CASE).containsMatchIn(this)
