package su.afk.yummy.tv.core.storage.allohatrack.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import su.afk.yummy.tv.core.storage.allohatrack.AllohaTrackPreferenceStorage
import su.afk.yummy.tv.core.storage.allohatrack.AllohaTrackPreferenceStore
import su.afk.yummy.tv.core.storage.db.AppDatabase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AllohaTrackPreferenceStorageModule {

    @Provides
    @Singleton
    internal fun provideAllohaTrackPreferenceStore(db: AppDatabase): AllohaTrackPreferenceStore =
        AllohaTrackPreferenceStore(db.allohaTrackPreferenceDao())

    @Provides
    @Singleton
    internal fun provideAllohaTrackPreferenceStorage(
        store: AllohaTrackPreferenceStore,
    ): AllohaTrackPreferenceStorage = store
}
