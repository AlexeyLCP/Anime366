package su.afk.yummy.tv.android.startup

import android.app.ActivityManager
import android.content.Context
import coil3.ImageLoader
import coil3.SingletonImageLoader
import coil3.annotation.ExperimentalCoilApi
import coil3.disk.DiskCache
import coil3.disk.directory
import coil3.memory.MemoryCache
import coil3.network.ktor3.KtorNetworkFetcherFactory
import coil3.request.crossfade
import dagger.hilt.android.qualifiers.ApplicationContext
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import okhttp3.OkHttpClient
import su.afk.yummy.tv.core.preferences.settings.CacheSettingsStore
import su.afk.yummy.tv.core.utils.kodik.KodikThumbnailCacheIO
import su.afk.yummy.tv.core.utils.kodik.KodikThumbnailFetcher
import su.afk.yummy.tv.core.utils.kodik.KodikThumbnailKeyer
import su.afk.yummy.tv.core.utils.kodik.ResolveKodikThumbnailUrlUseCase
import su.afk.yummy.tv.domain.anime.usecase.GetCachedAnimeVideosUseCase
import su.afk.yummy.tv.feature.library.thumbnail.HistoryEpisodeThumbnailFetcher
import su.afk.yummy.tv.feature.library.thumbnail.HistoryEpisodeThumbnailKeyer
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Собирает и устанавливает singleton Coil [ImageLoader]: размер кэша считаем от пользовательских
 * настроек и типа устройства (урезаем memory cache на low-RAM), плюс регистрируем кастомные
 * компоненты — Kodik fetcher/keyer и Ktor network fetcher поверх общего OkHttpClient приложения.
 * Kodik-превью серий читаются/пишутся не в этот кэш, а в собственный маленький [DiskCache] через
 * [KodikThumbnailCacheIO] (см. su.afk.yummy.tv.core.utils.kodik.di.KodikNetworkModule).
 */
@Singleton
class CoilImageLoaderInstaller @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val okHttpClient: OkHttpClient,
    private val settingsStore: CacheSettingsStore,
    private val resolveKodikThumbnailUrl: ResolveKodikThumbnailUrlUseCase,
    private val kodikThumbnailCacheIO: KodikThumbnailCacheIO,
    private val getCachedAnimeVideos: GetCachedAnimeVideosUseCase,
) {

    @OptIn(ExperimentalCoilApi::class)
    fun install() {
        val cacheBytes = settingsStore.currentPreviewCacheSize.toLong() * 1024L * 1024L
        val memoryCachePercent =
            if (isLowRamDevice()) LOW_RAM_MEMORY_CACHE_PERCENT else MEMORY_CACHE_PERCENT

        SingletonImageLoader.setSafe {
            // newBuilder(): общий пул соединений и диспетчер с API-клиентом,
            // но независимая конфигурация — картинки кэширует сам Coil.
            val imageHttpClient = HttpClient(OkHttp) {
                engine {
                    preconfigured = okHttpClient.newBuilder().build()
                }
            }
            ImageLoader.Builder(it)
                .crossfade(true)
                .memoryCache {
                    MemoryCache.Builder()
                        .maxSizePercent(context, memoryCachePercent)
                        .build()
                }
                .diskCache {
                    DiskCache.Builder()
                        .directory(context.cacheDir.resolve(IMAGE_CACHE_DIR_NAME))
                        .maxSizeBytes(cacheBytes)
                        .build()
                }
                .components {
                    add(KodikThumbnailKeyer())
                    add(
                        KodikThumbnailFetcher.Factory(
                            resolveKodikThumbnailUrl,
                            kodikThumbnailCacheIO
                        )
                    )
                    add(HistoryEpisodeThumbnailKeyer())
                    add(
                        HistoryEpisodeThumbnailFetcher.Factory(
                            getCachedAnimeVideos,
                            resolveKodikThumbnailUrl,
                            kodikThumbnailCacheIO,
                        )
                    )
                    add(KtorNetworkFetcherFactory(httpClient = imageHttpClient))
                }
                .build()
        }
    }

    private fun isLowRamDevice(): Boolean =
        (context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager)?.isLowRamDevice == true

    private companion object {
        const val IMAGE_CACHE_DIR_NAME = "image_cache"
        const val MEMORY_CACHE_PERCENT = 0.15
        const val LOW_RAM_MEMORY_CACHE_PERCENT = 0.10
    }
}
