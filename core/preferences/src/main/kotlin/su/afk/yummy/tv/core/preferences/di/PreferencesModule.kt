package su.afk.yummy.tv.core.preferences.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import su.afk.yummy.tv.core.preferences.auth.KeystoreYaniAuthPreferences
import su.afk.yummy.tv.core.preferences.auth.YaniAuthPreferences
import su.afk.yummy.tv.core.preferences.interface_mode.AppInterfaceModePreferences
import su.afk.yummy.tv.core.preferences.interface_mode.SharedPreferencesAppInterfaceModePreferences
import su.afk.yummy.tv.core.preferences.settings.DataStoreSettingsStore
import su.afk.yummy.tv.core.preferences.settings.SettingsStore
import su.afk.yummy.tv.core.utils.di.IoApplicationScope
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PreferencesModule {

    @Provides
    @Singleton
    fun provideSettingsStore(
        @ApplicationContext context: Context,
        @IoApplicationScope scope: CoroutineScope,
    ): SettingsStore = DataStoreSettingsStore(context, scope)

    @Provides
    @Singleton
    fun provideAppInterfaceModePreferences(
        @ApplicationContext context: Context,
    ): AppInterfaceModePreferences = SharedPreferencesAppInterfaceModePreferences(context)

    @Provides
    @Singleton
    fun provideYaniAuthPreferences(
        @ApplicationContext context: Context,
        settingsStore: SettingsStore,
    ): YaniAuthPreferences = KeystoreYaniAuthPreferences(context, settingsStore)
}
