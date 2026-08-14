package su.afk.yummy.tv.core.featuretoggle.api

import kotlinx.coroutines.flow.Flow

interface FeatureToggleUpdateObserver {
    val currentActivationId: Long
    val updates: Flow<Long>
}
