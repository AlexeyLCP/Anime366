package su.afk.yummy.tv.core.storage.anime.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import su.afk.yummy.tv.core.storage.anime.AnimeStorage
import su.afk.yummy.tv.core.storage.anime.AnimeStorageStore
import su.afk.yummy.tv.core.storage.db.AppDatabase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AnimeStorageModule {

    @Provides
    @Singleton
    internal fun provideAnimeStorageStore(db: AppDatabase): AnimeStorageStore =
        AnimeStorageStore(db.animeStorageDao())

    @Provides
    @Singleton
    internal fun provideAnimeStorage(store: AnimeStorageStore): AnimeStorage = store
}
