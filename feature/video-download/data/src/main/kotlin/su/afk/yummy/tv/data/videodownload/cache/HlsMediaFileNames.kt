package su.afk.yummy.tv.data.videodownload.cache

/**
 * Расширения медиафайлов HLS, чьё имя остаётся неизменным при ротации подписанного URL, — в отличие
 * от плейлистов, которые обязаны перечитываться на каждой ротации.
 */
private val STABLE_HLS_MEDIA_EXTENSIONS = listOf(
    ".m4s",
    ".ts",
    ".aac",
    ".mp4",
    ".key",
)

internal fun String.hasStableHlsMediaExtension(): Boolean =
    STABLE_HLS_MEDIA_EXTENSIONS.any { extension -> endsWith(extension, ignoreCase = true) }
