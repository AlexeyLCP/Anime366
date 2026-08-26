package su.afk.yummy.tv.feature.videodownload.playback

import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.cache.Cache
import androidx.media3.datasource.cache.CacheKeyFactory

/**
 * Playback-facing contract of the video download cache: gives the player access to the
 * offline downloads cache without exposing the video-download data layer.
 */
@OptIn(UnstableApi::class)
interface VideoDownloadPlaybackCache {

    /** Cache holding user-initiated downloads. */
    val cache: Cache

    /**
     * Фабрика ключей, которой загрузка была записана в кэш. Возвращает `null`, когда ключей у неё
     * нет вовсе (старые загрузки не-Alloha лежат под сырыми URL) — тогда фабрику ставить нельзя.
     *
     * [cacheKeyScheme] — `VideoDownloadCacheKeyScheme.storageValue` из записи загрузки;
     * [legacyRotating] учитывается только для схемы `0`, где признак «Alloha HLS» в базе не хранится.
     */
    fun downloadCacheKeyFactory(
        downloadCacheKey: String,
        manifestUri: String?,
        cacheKeyScheme: Int,
        legacyRotating: Boolean,
    ): CacheKeyFactory?
}
