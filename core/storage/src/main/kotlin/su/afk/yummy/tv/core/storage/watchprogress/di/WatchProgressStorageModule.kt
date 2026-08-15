package su.afk.yummy.tv.core.storage.watchprogress.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import su.afk.yummy.tv.core.storage.db.AppDatabase
import su.afk.yummy.tv.core.storage.watchprogress.WatchProgressStorage
import su.afk.yummy.tv.core.storage.watchprogress.WatchProgressStore
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object WatchProgressStorageModule {

    @Provides
    @Singleton
    internal fun provideWatchProgressStore(db: AppDatabase): WatchProgressStore =
        WatchProgressStore(db.watchProgressDao())

    @Provides
    @Singleton
    internal fun provideWatchProgressStorage(store: WatchProgressStore): WatchProgressStorage =
        store
}
