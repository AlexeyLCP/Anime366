package su.afk.yummy.tv.core.preferences.settings.datastore

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import su.afk.yummy.tv.core.model.settings.PreviewCacheSize
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
    private var previewCacheSizeSnapshot = PreviewCacheSize.MB_100

    /** Синхронное значение для Coil-кэша, который настраивается вне корутины. */
    override val currentPreviewCacheSize: PreviewCacheSize
        get() = previewCacheSizeSnapshot

    override val previewCacheSize: Flow<PreviewCacheSize> =
        store.data.map { prefs -> prefs.previewCacheSize() }

    init {
        scope.launch {
            previewCacheSize.collect { size -> previewCacheSizeSnapshot = size }
        }
    }

    override suspend fun setPreviewCacheSize(size: PreviewCacheSize) {
        previewCacheSizeSnapshot = size
        store.edit { prefs -> prefs[previewCacheSizeKey] = size.megabytes }
    }
}
