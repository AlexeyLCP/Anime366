package su.afk.yummy.tv.core.preferences.settings

import kotlinx.coroutines.flow.Flow
import su.afk.yummy.tv.core.model.settings.LastSearchSnapshot

/** Настройка «сохранять последний поиск»: флаг и снимок последнего запроса с фильтрами. */
interface SearchSettingsStore {

    val saveLastSearchEnabled: Flow<Boolean>
    val lastSearchSnapshot: Flow<LastSearchSnapshot>

    suspend fun setSaveLastSearchEnabled(enabled: Boolean)
    suspend fun setLastSearchSnapshot(snapshot: LastSearchSnapshot)
}
