package su.afk.yummy.tv.core.storage.document.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import su.afk.yummy.tv.core.storage.db.AppDatabase
import su.afk.yummy.tv.core.storage.document.DocumentCacheStorage
import su.afk.yummy.tv.core.storage.document.DocumentCacheStore
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DocumentStorageModule {

    @Provides
    @Singleton
    internal fun provideDocumentCacheStore(db: AppDatabase): DocumentCacheStore =
        DocumentCacheStore(db.documentCacheDao())

    @Provides
    @Singleton
    internal fun provideDocumentCacheStorage(store: DocumentCacheStore): DocumentCacheStorage =
        store
}
