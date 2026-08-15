package su.afk.yummy.tv.core.storage.schedule.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import su.afk.yummy.tv.core.storage.db.AppDatabase
import su.afk.yummy.tv.core.storage.schedule.AnimeScheduleStorage
import su.afk.yummy.tv.core.storage.schedule.AnimeScheduleStore
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ScheduleStorageModule {

    @Provides
    @Singleton
    internal fun provideAnimeScheduleStore(db: AppDatabase): AnimeScheduleStore =
        AnimeScheduleStore(db.animeScheduleDao())

    @Provides
    @Singleton
    internal fun provideAnimeScheduleStorage(store: AnimeScheduleStore): AnimeScheduleStorage =
        store
}
