package su.afk.yummy.tv.core.storage.subscriptionselection.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import su.afk.yummy.tv.core.storage.db.AppDatabase
import su.afk.yummy.tv.core.storage.subscriptionselection.VideoSubscriptionSelectionStorage
import su.afk.yummy.tv.core.storage.subscriptionselection.VideoSubscriptionSelectionStore
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object VideoSubscriptionSelectionStorageModule {

    @Provides
    @Singleton
    internal fun provideVideoSubscriptionSelectionStore(
        db: AppDatabase,
    ): VideoSubscriptionSelectionStore =
        VideoSubscriptionSelectionStore(db.videoSubscriptionSelectionDao())

    @Provides
    @Singleton
    internal fun provideVideoSubscriptionSelectionStorage(
        store: VideoSubscriptionSelectionStore,
    ): VideoSubscriptionSelectionStorage = store
}
