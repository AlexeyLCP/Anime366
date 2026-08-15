package su.afk.yummy.tv.core.storage.top.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import su.afk.yummy.tv.core.storage.db.AppDatabase
import su.afk.yummy.tv.core.storage.top.AnimeTopStorage
import su.afk.yummy.tv.core.storage.top.AnimeTopStore
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TopStorageModule {

    @Provides
    @Singleton
    internal fun provideAnimeTopStore(db: AppDatabase): AnimeTopStore =
        AnimeTopStore(db.animeTopDao())

    @Provides
    @Singleton
    internal fun provideAnimeTopStorage(store: AnimeTopStore): AnimeTopStorage = store
}
