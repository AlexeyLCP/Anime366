package su.afk.yummy.tv.android.di

import android.app.Application
import android.os.StrictMode
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import su.afk.yummy.tv.BuildConfig
import su.afk.yummy.tv.android.lifecycle.OnlineStatusCoordinator
import su.afk.yummy.tv.android.startup.AppStartupMaintenanceRunner
import su.afk.yummy.tv.android.startup.CoilImageLoaderInstaller
import su.afk.yummy.tv.core.analytics.api.initialize.AnalyticsInitializer
import su.afk.yummy.tv.core.featuretoggle.FeatureToggleRefreshCoordinator
import su.afk.yummy.tv.core.featuretoggle.api.FeatureToggleInitializer
import su.afk.yummy.tv.core.tv.HomeFeedRefreshScheduler
import javax.inject.Inject

@HiltAndroidApp
class YummyTvApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var homeFeedRefreshScheduler: HomeFeedRefreshScheduler

    @Inject
    lateinit var analyticsInitializer: AnalyticsInitializer

    @Inject
    lateinit var featureToggleInitializer: FeatureToggleInitializer

    @Inject
    lateinit var onlineStatusCoordinator: OnlineStatusCoordinator

    @Inject
    lateinit var featureToggleRefreshCoordinator: FeatureToggleRefreshCoordinator

    @Inject
    lateinit var coilImageLoaderInstaller: CoilImageLoaderInstaller

    @Inject
    lateinit var startupMaintenanceRunner: AppStartupMaintenanceRunner

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()

        installStrictModeIfDebug()
        setupAnalytics()
        setupFeatureToggles()
        coilImageLoaderInstaller.install()
        onlineStatusCoordinator.start()
        featureToggleRefreshCoordinator.start()
        homeFeedRefreshScheduler.schedule()
        startupMaintenanceRunner.run()
    }

    private fun installStrictModeIfDebug() {
        if (!BuildConfig.DEBUG) return
        StrictMode.setThreadPolicy(
            StrictMode.ThreadPolicy.Builder()
                .detectDiskReads()
                .detectDiskWrites()
                .detectNetwork()
                .penaltyLog()
                .build()
        )
    }

    private fun setupAnalytics() {
        analyticsInitializer.initialize(this, BuildConfig.APPMETRICA_API_KEY)
    }

    private fun setupFeatureToggles() {
        featureToggleInitializer.initialize(this, BuildConfig.VARIOQUB_CLIENT_ID)
    }
}
