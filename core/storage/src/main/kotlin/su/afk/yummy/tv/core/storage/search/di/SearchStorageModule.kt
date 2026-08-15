package su.afk.yummy.tv.core.storage.search.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import su.afk.yummy.tv.core.storage.db.AppDatabase
import su.afk.yummy.tv.core.storage.search.SearchStorage
import su.afk.yummy.tv.core.storage.search.SearchStorageStore
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SearchStorageModule {

    @Provides
    @Singleton
    internal fun provideSearchStorageStore(db: AppDatabase): SearchStorageStore =
        SearchStorageStore(db.searchStorageDao())

    @Provides
    @Singleton
    internal fun provideSearchStorage(store: SearchStorageStore): SearchStorage = store
}
