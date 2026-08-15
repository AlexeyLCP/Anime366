package su.afk.yummy.tv.core.utils.kodik

import coil3.ImageLoader
import coil3.fetch.FetchResult
import coil3.fetch.Fetcher
import coil3.request.Options

/**
 * Резолвит URL Kodik-превью и читает/пишет картинку через [KodikThumbnailCacheIO] —
 * отдельный, небольшой disk cache (не общий кэш постеров), ключ по iframe-урлу.
 */
class KodikThumbnailFetcher(
    private val data: KodikThumbnail,
    private val cacheIO: KodikThumbnailCacheIO,
    private val resolveKodikThumbnailUrl: ResolveKodikThumbnailUrlUseCase,
) : Fetcher {

    override suspend fun fetch(): FetchResult? {
        val resolvedUrl = resolveKodikThumbnailUrl(data.iframeUrl)
        return cacheIO.fetch(data.cacheKey, resolvedUrl)
    }

    class Factory(
        private val resolveKodikThumbnailUrl: ResolveKodikThumbnailUrlUseCase,
        private val cacheIO: KodikThumbnailCacheIO,
    ) : Fetcher.Factory<KodikThumbnail> {

        override fun create(
            data: KodikThumbnail,
            options: Options,
            imageLoader: ImageLoader,
        ): Fetcher = KodikThumbnailFetcher(
            data = data,
            cacheIO = cacheIO,
            resolveKodikThumbnailUrl = resolveKodikThumbnailUrl,
        )
    }
}
