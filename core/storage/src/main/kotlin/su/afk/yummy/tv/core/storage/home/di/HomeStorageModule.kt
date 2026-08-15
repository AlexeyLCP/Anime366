package su.afk.yummy.tv.core.storage.home.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import su.afk.yummy.tv.core.storage.db.AppDatabase
import su.afk.yummy.tv.core.storage.home.HomeFeedStorage
import su.afk.yummy.tv.core.storage.home.HomeFeedStore
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object HomeStorageModule {

    @Provides
    @Singleton
    internal fun provideHomeFeedStore(db: AppDatabase): HomeFeedStore =
        HomeFeedStore(db.homeFeedDao())

    @Provides
    @Singleton
    internal fun provideHomeFeedStorage(store: HomeFeedStore): HomeFeedStorage = store
}
