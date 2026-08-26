package su.afk.yummy.tv.core.preferences.settings

import kotlinx.coroutines.flow.Flow

/** Размер кэша превьюшек, МБ. */
interface CacheSettingsStore {

    val currentPreviewCacheSize: Int
    val previewCacheSize: Flow<Int>

    suspend fun setPreviewCacheSize(size: Int)
}
