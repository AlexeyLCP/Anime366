package su.afk.yummy.tv.data.watchlater.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import su.afk.yummy.tv.data.watchlater.repository.DefaultWatchLaterRepository
import su.afk.yummy.tv.domain.watchlater.repository.WatchLaterRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal interface WatchLaterDataModule {
    @Binds
    @Singleton
    fun bindWatchLaterRepository(repository: DefaultWatchLaterRepository): WatchLaterRepository
}
