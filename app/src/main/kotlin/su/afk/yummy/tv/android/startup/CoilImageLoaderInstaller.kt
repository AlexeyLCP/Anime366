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
import su.afk.yummy.tv.core.preferences.settings.SettingsStore
import su.afk.yummy.tv.core.utils.KodikThumbnailFetcher
import su.afk.yummy.tv.core.utils.KodikThumbnailKeyer
import su.afk.yummy.tv.core.utils.ResolveKodikThumbnailUrlUseCase
import su.afk.yummy.tv.domain.anime.usecase.GetAnimeVideosUseCase
import su.afk.yummy.tv.feature.library.thumbnail.HistoryEpisodeThumbnailFetcher
import su.afk.yummy.tv.feature.library.thumbnail.HistoryEpisodeThumbnailKeyer
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Собирает и устанавливает singleton Coil [ImageLoader]: размер кэша считаем от пользовательских
 * настроек и типа устройства (урезаем memory cache на low-RAM), плюс регистрируем кастомные
 * компоненты — Kodik fetcher/keyer и Ktor network fetcher поверх общего OkHttpClient приложения.
 */
@Singleton
class CoilImageLoaderInstaller @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val okHttpClient: OkHttpClient,
    private val settingsStore: SettingsStore,
    private val resolveKodikThumbnailUrl: ResolveKodikThumbnailUrlUseCase,
    private val getAnimeVideos: GetAnimeVideosUseCase,
) {

    @OptIn(ExperimentalCoilApi::class)
    fun install() {
        val cacheBytes = settingsStore.currentPreviewCacheSize.megabytes.toLong() * 1024L * 1024L
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
                    add(KodikThumbnailFetcher.Factory(resolveKodikThumbnailUrl))
                    add(HistoryEpisodeThumbnailKeyer())
                    add(
                        HistoryEpisodeThumbnailFetcher.Factory(
                            getAnimeVideos,
                            resolveKodikThumbnailUrl
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
