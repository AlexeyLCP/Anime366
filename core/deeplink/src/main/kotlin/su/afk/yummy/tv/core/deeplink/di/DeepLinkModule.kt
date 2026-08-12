package su.afk.yummy.tv.core.deeplink.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import su.afk.yummy.tv.core.deeplink.DeepLinkHandlerImpl
import su.afk.yummy.tv.core.deeplink.api.DeepLinkHandler
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal interface DeepLinkModule {

    @Binds
    @Singleton
    fun bindDeepLinkHandler(impl: DeepLinkHandlerImpl): DeepLinkHandler
}
