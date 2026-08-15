package su.afk.yummy.tv.core.storage.account.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import su.afk.yummy.tv.core.storage.account.AccountStorage
import su.afk.yummy.tv.core.storage.account.AccountStorageStore
import su.afk.yummy.tv.core.storage.db.AppDatabase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AccountStorageModule {

    @Provides
    @Singleton
    internal fun provideAccountStorageStore(db: AppDatabase): AccountStorageStore =
        AccountStorageStore(db.accountStorageDao())

    @Provides
    @Singleton
    internal fun provideAccountStorage(store: AccountStorageStore): AccountStorage = store
}
