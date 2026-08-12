package su.afk.yummy.tv.core.analytics.api

/**
 * Sends analytics events to the configured destination.
 */
interface AnalyticsTracker {
    /**
     * Reports an analytics event by name. Blank event names are ignored by concrete implementations.
     */
    fun track(eventName: String, params: Map<String, String> = emptyMap())

    /**
     * Reports a non-fatal error. Blank messages are ignored by concrete implementations.
     */
    fun reportError(
        message: String,
        throwable: Throwable,
        groupIdentifier: String? = null,
    )
}
