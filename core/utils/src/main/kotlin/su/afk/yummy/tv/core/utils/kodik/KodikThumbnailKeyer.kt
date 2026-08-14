package su.afk.yummy.tv.core.utils.kodik

import coil3.key.Keyer
import coil3.request.Options

/** Даёт Coil стабильный memory-cache key, не зависящий от конечного URL картинки. */
class KodikThumbnailKeyer : Keyer<KodikThumbnail> {

    override fun key(data: KodikThumbnail, options: Options): String = data.cacheKey
}
