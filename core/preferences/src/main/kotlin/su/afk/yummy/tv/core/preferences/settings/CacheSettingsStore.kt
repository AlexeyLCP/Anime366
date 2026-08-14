package su.afk.yummy.tv.core.preferences.settings

import kotlinx.coroutines.flow.Flow
import su.afk.yummy.tv.core.preferences.settings.model.PreviewCacheSize

/** Размер кэша превьюшек и разовые флаги его миграции. */
interface CacheSettingsStore {

    val currentPreviewCacheSize: PreviewCacheSize
    val previewCacheSize: Flow<PreviewCacheSize>

    suspend fun setPreviewCacheSize(size: PreviewCacheSize)

    /**
     * Returns `true` exactly once: the first time this is called after the streaming/download
     * cache split, so callers can prune the now-unbounded-legacy entries a single time. Every
     * later call returns `false`.
     */
    suspend fun consumeLegacyStreamingCachePruneFlag(): Boolean
}
