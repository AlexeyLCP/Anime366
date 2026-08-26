package su.afk.yummy.tv.data.videodownload.cache

import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.cache.CacheKeyFactory
import su.afk.yummy.tv.domain.videodownload.model.VideoDownloadCacheKeyScheme

/**
 * Единственная точка, где схема ключей превращается в фабрику. Загрузка, экспорт и воспроизведение
 * обязаны спрашивать именно её: любое расхождение означает, что скачанные данные не найдутся.
 *
 * [legacyRotating] важен только для [VideoDownloadCacheKeyScheme.Legacy]: у скачанного до перехода
 * на неймспейсинг признак «Alloha HLS» в базе не сохранён и по-прежнему выводится из источника.
 */
@OptIn(UnstableApi::class)
fun downloadCacheKeyFactoryFor(
    scheme: VideoDownloadCacheKeyScheme,
    downloadCacheKey: String,
    manifestUri: String?,
    legacyRotating: Boolean,
): CacheKeyFactory? = when (scheme) {
    VideoDownloadCacheKeyScheme.Legacy -> if (legacyRotating) {
        RotatingHlsCacheKeyFactory(
            downloadCacheKey = downloadCacheKey,
            manifestUri = manifestUri,
        )
    } else {
        // Сырые URL: ключи такой загрузки ничем не помечены, подменять их задним числом нельзя —
        // уже скачанные сегменты перестали бы находиться.
        null
    }

    VideoDownloadCacheKeyScheme.Namespaced -> DownloadCacheKeyFactory(
        downloadCacheKey = downloadCacheKey,
        manifestUri = manifestUri,
        stableMediaFileNames = false,
    )

    VideoDownloadCacheKeyScheme.NamespacedRotating -> DownloadCacheKeyFactory(
        downloadCacheKey = downloadCacheKey,
        manifestUri = manifestUri,
        stableMediaFileNames = true,
    )
}

/** Префиксы, под которыми могут лежать ресурсы загрузки с данным [downloadCacheKey]. */
internal fun downloadResourcePrefixes(downloadCacheKey: String): List<String> = listOf(
    RotatingHlsCacheKeyFactory.resourcePrefix(downloadCacheKey),
    DownloadCacheKeyFactory.resourcePrefix(downloadCacheKey),
)
