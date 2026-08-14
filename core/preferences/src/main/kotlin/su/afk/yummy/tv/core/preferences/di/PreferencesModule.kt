package su.afk.yummy.tv.core.preferences.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import su.afk.yummy.tv.core.preferences.auth.KeystoreYaniAuthPreferences
import su.afk.yummy.tv.core.preferences.auth.YaniAuthPreferences
import su.afk.yummy.tv.core.preferences.interface_mode.AppInterfaceModePreferences
import su.afk.yummy.tv.core.preferences.interface_mode.SharedPreferencesAppInterfaceModePreferences
import su.afk.yummy.tv.core.preferences.settings.AppLifecycleSettingsStore
import su.afk.yummy.tv.core.preferences.settings.AppearanceSettingsStore
import su.afk.yummy.tv.core.preferences.settings.CacheSettingsStore
import su.afk.yummy.tv.core.preferences.settings.DataStoreSettingsStore
import su.afk.yummy.tv.core.preferences.settings.PlayerSettingsStore
import su.afk.yummy.tv.core.preferences.settings.SettingsStore
import su.afk.yummy.tv.core.preferences.settings.VideoExportSettingsStore
import su.afk.yummy.tv.core.preferences.settings.YaniAccountSettingsStore
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal interface PreferencesModule {

    @Binds
    @Singleton
    fun bindSettingsStore(impl: DataStoreSettingsStore): SettingsStore

    @Binds
    @Singleton
    fun bindYaniAccountSettingsStore(impl: DataStoreSettingsStore): YaniAccountSettingsStore

    @Binds
    @Singleton
    fun bindPlayerSettingsStore(impl: DataStoreSettingsStore): PlayerSettingsStore

    @Binds
    @Singleton
    fun bindAppearanceSettingsStore(impl: DataStoreSettingsStore): AppearanceSettingsStore

    @Binds
    @Singleton
    fun bindCacheSettingsStore(impl: DataStoreSettingsStore): CacheSettingsStore

    @Binds
    @Singleton
    fun bindVideoExportSettingsStore(impl: DataStoreSettingsStore): VideoExportSettingsStore

    @Binds
    @Singleton
    fun bindAppLifecycleSettingsStore(impl: DataStoreSettingsStore): AppLifecycleSettingsStore

    @Binds
    @Singleton
    fun bindAppInterfaceModePreferences(
        impl: SharedPreferencesAppInterfaceModePreferences,
    ): AppInterfaceModePreferences

    @Binds
    @Singleton
    fun bindYaniAuthPreferences(impl: KeystoreYaniAuthPreferences): YaniAuthPreferences
}
