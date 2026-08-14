package su.afk.yummy.tv.core.preferences.settings

import kotlinx.coroutines.flow.Flow
import su.afk.yummy.tv.core.preferences.settings.model.DetailsButtonAction
import su.afk.yummy.tv.core.preferences.settings.model.MainSettingsSnapshot
import su.afk.yummy.tv.core.preferences.settings.model.SettingsSnapshot

/**
 * Фасад над доменными хранилищами настроек ([YaniAccountSettingsStore], [PlayerSettingsStore],
 * [AppearanceSettingsStore], [CacheSettingsStore], [VideoExportSettingsStore],
 * [AppLifecycleSettingsStore]) для кода, которому нужны поля сразу нескольких доменов
 * (например, [settingsSnapshot]/[mainSettingsSnapshot] агрегируют их). Однодоменным
 * потребителям следует зависеть от узкого интерфейса напрямую.
 */
interface SettingsStore :
    YaniAccountSettingsStore,
    PlayerSettingsStore,
    AppearanceSettingsStore,
    CacheSettingsStore,
    VideoExportSettingsStore,
    AppLifecycleSettingsStore {

    val settingsSnapshot: Flow<SettingsSnapshot>
    val mainSettingsSnapshot: Flow<MainSettingsSnapshot>

    companion object {
        val defaultDetailsButtonOrder: List<DetailsButtonAction>
            get() = AppearanceSettingsStore.defaultDetailsButtonOrder
    }
}
