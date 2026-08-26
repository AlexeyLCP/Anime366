package su.afk.yummy.tv.data.videodownload.cache

import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DataSpec
import androidx.media3.datasource.cache.CacheKeyFactory

/**
 * Кладёт все ресурсы одной загрузки в её собственный неймспейс, производный от `cacheKey`.
 *
 * Без этого HLS/DASH-загрузка вообще не привязана к своей записи: `HlsDownloader` строит `DataSpec`
 * из сырых URI и `MediaItem.customCacheKey` к ним не применяет (его учитывает только
 * `ProgressiveDownloader`). Из-за этого удалить данные одной серии точечно было невозможно, а
 * попытка вычистить «осиротевшее» сносила данные всех остальных серий.
 *
 * [manifestUri] — тот самый URI, которым верхнеуровневый манифест забирается на загрузке; его
 * ключом становится сам [downloadCacheKey], чтобы `manifestKeyToEvictOnRefresh` реально выселял
 * протухший плейлист, а воспроизведение находило манифест по `customCacheKey`.
 *
 * [stableMediaFileNames] включается только для источников с ротирующимися подписанными URL (Alloha
 * HLS): там идентичностью медиафайла служит его имя, иначе — полный URI. Имя файла как идентичность
 * безопасно лишь при ротации: в обычном multivariant-потоке сегменты разных дорожек могут
 * называться одинаково и склеились бы в один ресурс.
 */
@OptIn(UnstableApi::class)
class DownloadCacheKeyFactory(
    private val downloadCacheKey: String,
    private val manifestUri: String?,
    private val stableMediaFileNames: Boolean,
) : CacheKeyFactory {
    override fun buildCacheKey(dataSpec: DataSpec): String {
        if (manifestUri != null && dataSpec.uri.toString() == manifestUri) return downloadCacheKey
        if (dataSpec.key == downloadCacheKey) return downloadCacheKey

        val fileName = dataSpec.uri.lastPathSegment
            ?.substringBefore('?')
            ?.takeIf { it.isNotBlank() }
        val identity = if (stableMediaFileNames && fileName?.hasStableHlsMediaExtension() == true) {
            fileName
        } else {
            // Вложенные плейлисты Alloha сознательно попадают сюда: их URI ротируется вместе с
            // подписью, поэтому каждый рефреш перечитывает свежий список сегментов.
            CacheKeyFactory.DEFAULT.buildCacheKey(dataSpec)
        }
        return resourcePrefix(downloadCacheKey) + identity
    }

    companion object {
        fun resourcePrefix(downloadCacheKey: String): String =
            "$downloadCacheKey$DOWNLOAD_RESOURCE_SEPARATOR"

        private const val DOWNLOAD_RESOURCE_SEPARATOR = "|dl-res|"
    }
}
