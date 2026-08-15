package su.afk.yummy.tv.core.storage.maintenance.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import su.afk.yummy.tv.core.storage.db.AppDatabase
import su.afk.yummy.tv.core.storage.maintenance.StorageCleanup
import su.afk.yummy.tv.core.storage.maintenance.StorageCleanupStore
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MaintenanceStorageModule {

    @Provides
    @Singleton
    internal fun provideStorageCleanupStore(db: AppDatabase): StorageCleanupStore =
        StorageCleanupStore(db.storageCleanupDao())

    @Provides
    @Singleton
    internal fun provideStorageCleanup(store: StorageCleanupStore): StorageCleanup = store
}
