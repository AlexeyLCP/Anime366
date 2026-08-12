package su.afk.yummy.tv.core.analytics.api.coroutine

/**
 * Reports app errors that should be visible in analytics and crash diagnostics.
 */
interface ErrorCoroutineAnalytics {
    fun reportCoroutineError(owner: String, throwable: Throwable)
}