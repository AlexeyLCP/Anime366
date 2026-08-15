package su.afk.yummy.tv.core.storage.collection.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import su.afk.yummy.tv.core.storage.collection.CollectionStorage
import su.afk.yummy.tv.core.storage.collection.CollectionStorageStore
import su.afk.yummy.tv.core.storage.db.AppDatabase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CollectionStorageModule {

    @Provides
    @Singleton
    internal fun provideCollectionStorageStore(db: AppDatabase): CollectionStorageStore =
        CollectionStorageStore(db.collectionStorageDao())

    @Provides
    @Singleton
    internal fun provideCollectionStorage(store: CollectionStorageStore): CollectionStorage = store
}
