package su.afk.yummy.tv.core.featuretoggle.varioqub

import android.content.Context
import android.os.StrictMode
import com.yandex.varioqub.analyticadapter.VarioqubConfigAdapter
import com.yandex.varioqub.appmetricaadapter.AppMetricaAdapter
import com.yandex.varioqub.config.FetchError
import com.yandex.varioqub.config.OnFetchCompleteListener
import com.yandex.varioqub.config.Varioqub
import com.yandex.varioqub.config.VarioqubSettings
import su.afk.yummy.tv.core.analytics.api.AnalyticsTracker
import su.afk.yummy.tv.core.featuretoggle.BuildConfig
import su.afk.yummy.tv.core.featuretoggle.api.FeatureToggleInitializer
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class VarioqubFeatureToggleInitializer @Inject constructor(
    private val featureToggleState: VarioqubFeatureToggleState,
    private val analyticsTracker: AnalyticsTracker,
) : FeatureToggleInitializer {

    override fun initialize(context: Context, clientId: String) {
        val normalizedClientId = clientId.trim()
        if (normalizedClientId.isEmpty()) {
            featureToggleState.markNotInitialized()
            return
        }

        runCatching {
            val throttleIntervalSeconds = if (BuildConfig.DEBUG) {
                DEBUG_THROTTLE_INTERVAL_SECONDS
            } else {
                RELEASE_THROTTLE_INTERVAL_SECONDS
            }
            val settingsBuilder = VarioqubSettings.Builder(normalizedClientId)
                .withThrottleInterval(throttleIntervalSeconds)
            if (BuildConfig.DEBUG) {
                settingsBuilder.withLogs()
            }
            val settings = settingsBuilder.build()
            val adapter = if (BuildConfig.DEBUG) {
                NoAnalyticsVarioqubAdapter()
            } else {
                AppMetricaAdapter(context)
            }
            initializeVarioqub(settings, adapter, context)
            featureToggleState.markInitialized()
            activateConfig()
            fetchConfig()
        }.onFailure { throwable ->
            featureToggleState.markNotInitialized()
            analyticsTracker.log(TAG, throwable) { "Failed to initialize feature toggles" }
        }
    }

    override fun refresh() {
        if (!featureToggleState.isInitialized) return
        fetchConfig()
    }

    private fun initializeVarioqub(
        settings: VarioqubSettings,
        adapter: VarioqubConfigAdapter,
        context: Context,
    ) {
        if (!BuildConfig.DEBUG) {
            Varioqub.init(settings, adapter, context)
            return
        }

        val previousPolicy = StrictMode.allowThreadDiskReads()
        try {
            Varioqub.init(settings, adapter, context)
        } finally {
            StrictMode.setThreadPolicy(previousPolicy)
        }
    }

    private fun fetchConfig() {
        Varioqub.fetchConfig(object : OnFetchCompleteListener {
            override fun onSuccess() {
                analyticsTracker.log(TAG) { "Feature toggles fetched successfully" }
                activateConfig()
            }

            override fun onError(message: String, error: FetchError) {
                analyticsTracker.log(TAG) { "Failed to fetch feature toggles: $error $message" }
            }
        })
    }

    private fun activateConfig() {
        Varioqub.activateConfig {
            analyticsTracker.log(TAG) { "Feature toggles activated" }
            featureToggleState.notifyConfigActivated()
        }
    }

    private companion object {
        const val TAG = "FeatureToggles"
        const val DEBUG_THROTTLE_INTERVAL_SECONDS = 2L
        const val RELEASE_THROTTLE_INTERVAL_SECONDS = 60L
    }
}
