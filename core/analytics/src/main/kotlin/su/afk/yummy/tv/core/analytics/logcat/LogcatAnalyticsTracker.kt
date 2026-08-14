package su.afk.yummy.tv.core.analytics.logcat

import android.util.Log
import su.afk.yummy.tv.core.analytics.api.AnalyticsTracker
import javax.inject.Inject

internal class LogcatAnalyticsTracker @Inject constructor() : AnalyticsTracker {

    override fun track(eventName: String, params: Map<String, String>) {
        val eventName = eventName.trim()
        if (eventName.isEmpty()) return
        if (params.isEmpty()) {
            log(TAG, null) { "Would send analytics event: $eventName" }
        } else {
            log(TAG, null) { "Would send analytics event: $eventName, params=$params" }
        }
    }

    override fun reportError(
        message: String,
        throwable: Throwable,
        groupIdentifier: String?,
    ) {
        val message = message.trim()
        if (message.isEmpty()) return
        log(TAG, throwable) {
            "Would send analytics error: message=$message, group=$groupIdentifier"
        }
    }

    override fun log(tag: String, throwable: Throwable?, message: () -> String) {
        if (throwable == null) {
            Log.println(Log.DEBUG, tag, message())
        } else {
            Log.println(Log.DEBUG, tag, "${message()}\n${Log.getStackTraceString(throwable)}")
        }
    }

    private companion object {
        const val TAG = "Analytics"
    }
}
