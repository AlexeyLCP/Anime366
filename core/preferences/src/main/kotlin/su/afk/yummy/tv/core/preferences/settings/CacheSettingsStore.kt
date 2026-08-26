package su.afk.yummy.tv.core.preferences.settings

import kotlinx.coroutines.flow.Flow
import su.afk.yummy.tv.core.model.settings.PreviewCacheSize

/** Размер кэша превьюшек. */
interface CacheSettingsStore {

    val currentPreviewCacheSize: PreviewCacheSize
    val previewCacheSize: Flow<PreviewCacheSize>

    suspend fun setPreviewCacheSize(size: PreviewCacheSize)
}
