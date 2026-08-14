package su.afk.yummy.tv.android.startup

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import su.afk.yummy.tv.BuildConfig
import su.afk.yummy.tv.core.preferences.settings.SettingsStore
import su.afk.yummy.tv.core.storage.maintenance.StorageCleanupStore
import su.afk.yummy.tv.core.utils.coroutines.di.DefaultApplicationScope
import su.afk.yummy.tv.data.videodownload.cache.LegacyStreamingCachePruner
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Разовые фоновые задачи холодного старта, не блокирующие onCreate: языковые настройки,
 * чистка APK после самообновления и устаревших/legacy-кэшей.
 */
@Singleton
class AppStartupMaintenanceRunner @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val settingsStore: SettingsStore,
    private val legacyStreamingCachePruner: LegacyStreamingCachePruner,
    private val storageCleanupStore: StorageCleanupStore,
    @DefaultApplicationScope private val scope: CoroutineScope,
) {

    fun run() {
        scope.launch {
            settingsStore.ensureYaniContentLanguageInitialized()
            if (settingsStore.markStartedVersion(BuildConfig.VERSION_CODE)) {
                deleteDownloadedUpdateApk()
            }
            if (settingsStore.consumeLegacyStreamingCachePruneFlag()) {
                runCatching { legacyStreamingCachePruner.pruneOrphanedEntries() }
            }
            runCatching { storageCleanupStore.purgeStaleCaches() }
        }
    }

    private fun deleteDownloadedUpdateApk() {
        File(context.cacheDir, UPDATE_APK_FILE_NAME).delete()
    }

    private companion object {
        const val UPDATE_APK_FILE_NAME = "update.apk"
    }
}
