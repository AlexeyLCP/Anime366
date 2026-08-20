package su.afk.yummy.tv.core.preferences.settings.datastore

import androidx.datastore.preferences.core.Preferences
import su.afk.yummy.tv.core.model.settings.PreviewCacheSize
import su.afk.yummy.tv.core.preferences.settings.SettingsPreferenceKeys.previewCacheSizeKey

/** Размер кэша хранится в мегабайтах, а не именем константы — резолвим обратно в enum. */
internal fun Preferences.previewCacheSize(): PreviewCacheSize {
    val megabytes = this[previewCacheSizeKey] ?: PreviewCacheSize.MB_100.megabytes
    return PreviewCacheSize.entries.firstOrNull { it.megabytes == megabytes }
        ?: PreviewCacheSize.MB_100
}
