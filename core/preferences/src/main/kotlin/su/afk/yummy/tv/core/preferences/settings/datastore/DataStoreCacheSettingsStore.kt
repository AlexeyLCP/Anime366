package su.afk.yummy.tv.core.preferences.settings.datastore

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import su.afk.yummy.tv.core.preferences.settings.CacheSettingsStore
import su.afk.yummy.tv.core.preferences.settings.SettingsPreferenceKeys.previewCacheSizeKey
import su.afk.yummy.tv.core.utils.coroutines.di.IoApplicationScope
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class DataStoreCacheSettingsStore @Inject constructor(
    private val store: SettingsDataStore,
    @IoApplicationScope scope: CoroutineScope,
) : CacheSettingsStore {

    @Volatile
    private var previewCacheSizeSnapshot = DEFAULT_PREVIEW_CACHE_SIZE_MB

    /** Синхронное значение для Coil-кэша, который настраивается вне корутины. */
    override val currentPreviewCacheSize: Int
        get() = previewCacheSizeSnapshot

    override val previewCacheSize: Flow<Int> = store.data.map { prefs ->
        (prefs[previewCacheSizeKey] ?: DEFAULT_PREVIEW_CACHE_SIZE_MB)
            .coerceIn(MIN_PREVIEW_CACHE_SIZE_MB, MAX_PREVIEW_CACHE_SIZE_MB)
    }

    init {
        scope.launch {
            previewCacheSize.collect { size -> previewCacheSizeSnapshot = size }
        }
    }

    override suspend fun setPreviewCacheSize(size: Int) {
        val clamped = size.coerceIn(MIN_PREVIEW_CACHE_SIZE_MB, MAX_PREVIEW_CACHE_SIZE_MB)
        previewCacheSizeSnapshot = clamped
        store.edit { prefs -> prefs[previewCacheSizeKey] = clamped }
    }

    private companion object {
        const val DEFAULT_PREVIEW_CACHE_SIZE_MB = 100
        const val MIN_PREVIEW_CACHE_SIZE_MB = 50
        const val MAX_PREVIEW_CACHE_SIZE_MB = 500
    }
}
