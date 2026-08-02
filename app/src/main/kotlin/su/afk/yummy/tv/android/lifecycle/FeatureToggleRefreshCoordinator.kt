package su.afk.yummy.tv.android.lifecycle

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import su.afk.yummy.tv.core.featuretoggle.FeatureToggleInitializer
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FeatureToggleRefreshCoordinator @Inject constructor(
    private val featureToggleInitializer: FeatureToggleInitializer,
) : DefaultLifecycleObserver {

    fun start() {
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    override fun onStart(owner: LifecycleOwner) {
        featureToggleInitializer.refresh()
    }
}
