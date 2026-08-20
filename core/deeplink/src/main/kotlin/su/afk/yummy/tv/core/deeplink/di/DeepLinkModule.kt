package su.afk.yummy.tv.core.deeplink.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import su.afk.yummy.tv.core.deeplink.DeepLinkHandlerImpl
import su.afk.yummy.tv.core.deeplink.api.DeepLinkHandler
import su.afk.yummy.tv.core.deeplink.api.DeepLinkResolver
import su.afk.yummy.tv.core.deeplink.resolver.RootTabDeepLinkResolver
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal interface DeepLinkModule {

    @Binds
    @Singleton
    fun bindDeepLinkHandler(impl: DeepLinkHandlerImpl): DeepLinkHandler

    @Binds
    @IntoSet
    fun bindRootTabDeepLinkResolver(impl: RootTabDeepLinkResolver): DeepLinkResolver
}
