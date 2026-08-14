package su.afk.yummy.tv.core.featuretoggle.api

import android.content.Context

interface FeatureToggleInitializer {
    fun initialize(context: Context, clientId: String)
    fun refresh()
}
