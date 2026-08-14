package su.afk.yummy.tv.core.utils.coroutines.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import su.afk.yummy.tv.core.utils.coroutines.defaultScope
import su.afk.yummy.tv.core.utils.coroutines.ioScope
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CoroutineScopesModule {

    @Provides
    @Singleton
    @IoApplicationScope
    fun provideIoApplicationScope(): CoroutineScope = ioScope()

    @Provides
    @Singleton
    @DefaultApplicationScope
    fun provideDefaultApplicationScope(): CoroutineScope = defaultScope()
}
