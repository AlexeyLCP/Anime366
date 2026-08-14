package su.afk.yummy.tv.core.featuretoggle.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import su.afk.yummy.tv.core.featuretoggle.FeatureToggleVersionSupportChecker
import su.afk.yummy.tv.core.featuretoggle.api.FeatureToggleInitializer
import su.afk.yummy.tv.core.featuretoggle.api.FeatureToggleProvider
import su.afk.yummy.tv.core.featuretoggle.api.FeatureToggleUpdateObserver
import su.afk.yummy.tv.core.featuretoggle.api.VersionSupportChecker
import su.afk.yummy.tv.core.featuretoggle.varioqub.VarioqubFeatureToggleInitializer
import su.afk.yummy.tv.core.featuretoggle.varioqub.VarioqubFeatureToggleProvider
import su.afk.yummy.tv.core.featuretoggle.varioqub.VarioqubFeatureToggleState
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal interface FeatureToggleModule {

    @Binds
    @Singleton
    fun bindFeatureToggleInitializer(
        implementation: VarioqubFeatureToggleInitializer,
    ): FeatureToggleInitializer

    @Binds
    @Singleton
    fun bindFeatureToggleProvider(
        implementation: VarioqubFeatureToggleProvider,
    ): FeatureToggleProvider

    @Binds
    @Singleton
    fun bindVersionSupportChecker(
        implementation: FeatureToggleVersionSupportChecker,
    ): VersionSupportChecker

    @Binds
    @Singleton
    fun bindFeatureToggleUpdateObserver(
        implementation: VarioqubFeatureToggleState,
    ): FeatureToggleUpdateObserver
}
