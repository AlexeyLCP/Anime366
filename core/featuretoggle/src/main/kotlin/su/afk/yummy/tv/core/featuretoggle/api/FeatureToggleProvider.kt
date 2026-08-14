package su.afk.yummy.tv.core.featuretoggle.api

interface FeatureToggleProvider {
    fun isEnabled(flag: FeatureFlag.BooleanFlag): Boolean

    fun getString(flag: FeatureFlag.StringFlag): String
}
