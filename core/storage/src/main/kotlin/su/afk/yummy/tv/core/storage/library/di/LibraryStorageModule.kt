package su.afk.yummy.tv.core.storage.library.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import su.afk.yummy.tv.core.storage.db.AppDatabase
import su.afk.yummy.tv.core.storage.library.LibraryStorage
import su.afk.yummy.tv.core.storage.library.LibraryStore
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LibraryStorageModule {

    @Provides
    @Singleton
    internal fun provideLibraryStore(db: AppDatabase): LibraryStore = LibraryStore(db.libraryDao())

    @Provides
    @Singleton
    internal fun provideLibraryStorage(store: LibraryStore): LibraryStorage = store
}
