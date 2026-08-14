package su.afk.yummy.tv.core.preferences.interface_mode

/** Синхронное хранилище: читается на старте до setContent, поэтому не DataStore. */
interface AppInterfaceModePreferences {

    val selectedMode: AppInterfaceMode?

    fun select(mode: AppInterfaceMode)
}
