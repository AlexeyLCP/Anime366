package su.afk.yummy.tv.feature.library.thumbnail

import coil3.ImageLoader
import coil3.fetch.FetchResult
import coil3.fetch.Fetcher
import coil3.request.Options
import su.afk.yummy.tv.core.model.anime.kodikThumbnailIframeUrl
import su.afk.yummy.tv.core.model.anime.utils.episodeGroupKey
import su.afk.yummy.tv.core.utils.ResolveKodikThumbnailUrlUseCase
import su.afk.yummy.tv.domain.anime.usecase.GetAnimeVideosUseCase

/**
 * Резолвит kodik-превью серии из истории просмотров, у которой нет iframe-урла под рукой:
 * сначала подтягивает список видео аниме (кэш 5 мин в [GetAnimeVideosUseCase]), находит серию по
 * [episodeGroupKey], берёт её kodik iframe-урл и уже его отдаёт [ResolveKodikThumbnailUrlUseCase] —
 * дальше как в [su.afk.yummy.tv.core.utils.KodikThumbnailFetcher].
 */
class HistoryEpisodeThumbnailFetcher(
    private val data: HistoryEpisodeThumbnail,
    private val options: Options,
    private val imageLoader: ImageLoader,
    private val getAnimeVideos: GetAnimeVideosUseCase,
    private val resolveKodikThumbnailUrl: ResolveKodikThumbnailUrlUseCase,
) : Fetcher {

    override suspend fun fetch(): FetchResult? {
        val videos = runCatching { getAnimeVideos(data.animeId) }.getOrDefault(emptyList())
        val episodeVideos = videos.filter {
            it.episode.episodeGroupKey() == data.episode.episodeGroupKey()
        }
        val iframeUrl = episodeVideos.kodikThumbnailIframeUrl()
        val resolvedUrl = iframeUrl?.let { resolveKodikThumbnailUrl(it) }
        val delegatedOptions = options.copy(diskCacheKey = data.cacheKey)
        val mappedData = imageLoader.components.map(
            data = resolvedUrl ?: FALLBACK_URL,
            options = delegatedOptions,
        )
        val delegatedFetcher = checkNotNull(
            imageLoader.components.newFetcher(mappedData, delegatedOptions, imageLoader)?.first
        ) { "No Coil fetcher registered for History episode thumbnail" }
        return delegatedFetcher.fetch()
    }

    class Factory(
        private val getAnimeVideos: GetAnimeVideosUseCase,
        private val resolveKodikThumbnailUrl: ResolveKodikThumbnailUrlUseCase,
    ) : Fetcher.Factory<HistoryEpisodeThumbnail> {

        override fun create(
            data: HistoryEpisodeThumbnail,
            options: Options,
            imageLoader: ImageLoader,
        ): Fetcher = HistoryEpisodeThumbnailFetcher(
            data = data,
            options = options,
            imageLoader = imageLoader,
            getAnimeVideos = getAnimeVideos,
            resolveKodikThumbnailUrl = resolveKodikThumbnailUrl,
        )
    }

    private companion object {
        const val FALLBACK_URL = "https://offline.invalid/history-episode-thumbnail"
    }
}
