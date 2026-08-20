package su.afk.yummy.tv.feature.commonscreen.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import su.afk.yummy.tv.core.error.api.ErrorDestinationFactory
import su.afk.yummy.tv.core.navigation.registrar.NavRegistrar
import su.afk.yummy.tv.feature.commonscreen.errorScreen.ErrorNavigator
import su.afk.yummy.tv.feature.commonscreen.errorScreen.ErrorNavigatorRegister
import su.afk.yummy.tv.feature.commonscreen.navigator.ImageViewNavigator
import su.afk.yummy.tv.feature.commonscreen.navigator.ImageViewNavigatorRegister
import su.afk.yummy.tv.feature.commonscreen.navigator.IImageViewNavigator
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface NavigationModule {

    @Binds
    @IntoSet
    fun bindErrorNavigatorRegister(impl: ErrorNavigatorRegister): NavRegistrar

    @Binds
    @Singleton
    fun bindErrorNavigator(impl: ErrorNavigator): ErrorDestinationFactory

    @Binds
    @IntoSet
    fun bindImageViewNavigatorRegister(impl: ImageViewNavigatorRegister): NavRegistrar

    @Binds
    @Singleton
    fun bindImageViewNavigator(impl: ImageViewNavigator): IImageViewNavigator
}