package su.afk.yummy.tv.core.storage.comments.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import su.afk.yummy.tv.core.storage.comments.CommentsStorage
import su.afk.yummy.tv.core.storage.comments.CommentsStorageStore
import su.afk.yummy.tv.core.storage.db.AppDatabase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CommentsStorageModule {

    @Provides
    @Singleton
    internal fun provideCommentsStorageStore(db: AppDatabase): CommentsStorageStore =
        CommentsStorageStore(db.commentsStorageDao())

    @Provides
    @Singleton
    internal fun provideCommentsStorage(store: CommentsStorageStore): CommentsStorage = store
}
