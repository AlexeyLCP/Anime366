package su.afk.yummy.tv.core.preferences.settings

import kotlinx.coroutines.flow.Flow

/** Экспорт видео на устройство: каталог назначения и автозапуск. */
interface VideoExportSettingsStore {

    val videoExportDirectoryUri: Flow<String>
    val videoExportDirectoryName: Flow<String>
    val videoExportAutoEnabled: Flow<Boolean>

    suspend fun setVideoExportDirectory(uri: String, displayName: String)
    suspend fun setVideoExportAutoEnabled(enabled: Boolean)
}
