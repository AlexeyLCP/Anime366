package su.afk.yummy.tv.core.analytics.logcat

import android.content.Context
import su.afk.yummy.tv.core.analytics.api.initialize.AnalyticsInitializer
import javax.inject.Inject

internal class NoOpAnalyticsInitializer @Inject constructor() : AnalyticsInitializer {

    override fun initialize(context: Context, apiKey: String) = Unit
}