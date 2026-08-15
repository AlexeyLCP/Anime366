package su.afk.yummy.tv.core.storage.videodownload.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import su.afk.yummy.tv.core.storage.db.AppDatabase
import su.afk.yummy.tv.core.storage.videodownload.VideoDownloadStorage
import su.afk.yummy.tv.core.storage.videodownload.VideoDownloadStore
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object VideoDownloadStorageModule {

    @Provides
    @Singleton
    internal fun provideVideoDownloadStore(db: AppDatabase): VideoDownloadStore =
        VideoDownloadStore(db.videoDownloadDao())

    @Provides
    @Singleton
    internal fun provideVideoDownloadStorage(store: VideoDownloadStore): VideoDownloadStorage =
        store
}
