package su.afk.yummy.tv.core.error.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import su.afk.yummy.tv.core.error.AndroidStringProvider
import su.afk.yummy.tv.core.error.ErrorHandlerUseCaseImpl
import su.afk.yummy.tv.core.error.api.IErrorHandlerUseCase
import su.afk.yummy.tv.core.error.api.RetryStorage
import su.afk.yummy.tv.core.error.api.StringProvider
import su.afk.yummy.tv.core.error.storage.RetryStorageImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal interface ErrorModule {

    @Binds
    fun bindStringProvider(impl: AndroidStringProvider): StringProvider

    @Binds
    fun bindErrorHandlerUseCase(impl: ErrorHandlerUseCaseImpl): IErrorHandlerUseCase

    @Binds
    @Singleton
    fun bindRetryStorage(impl: RetryStorageImpl): RetryStorage
}