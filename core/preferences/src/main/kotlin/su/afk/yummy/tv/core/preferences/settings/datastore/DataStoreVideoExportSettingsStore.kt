package su.afk.yummy.tv.core.preferences.settings.datastore

import kotlinx.coroutines.flow.Flow
import su.afk.yummy.tv.core.preferences.settings.SettingsPreferenceKeys.videoExportAutoEnabledKey
import su.afk.yummy.tv.core.preferences.settings.SettingsPreferenceKeys.videoExportDirectoryNameKey
import su.afk.yummy.tv.core.preferences.settings.SettingsPreferenceKeys.videoExportDirectoryUriKey
import su.afk.yummy.tv.core.preferences.settings.VideoExportSettingsStore
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class DataStoreVideoExportSettingsStore @Inject constructor(
    private val store: SettingsDataStore,
) : VideoExportSettingsStore {

    override val videoExportDirectoryUri: Flow<String> = store.string(videoExportDirectoryUriKey)

    override val videoExportDirectoryName: Flow<String> = store.string(videoExportDirectoryNameKey)

    override val videoExportAutoEnabled: Flow<Boolean> =
        store.boolean(videoExportAutoEnabledKey, false)

    override suspend fun setVideoExportDirectory(uri: String, displayName: String) {
        store.edit { prefs ->
            prefs[videoExportDirectoryUriKey] = uri
            prefs[videoExportDirectoryNameKey] = displayName
        }
    }

    override suspend fun setVideoExportAutoEnabled(enabled: Boolean) =
        store.setBoolean(videoExportAutoEnabledKey, enabled)
}
