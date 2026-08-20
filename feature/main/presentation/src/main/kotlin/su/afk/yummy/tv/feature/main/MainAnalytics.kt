package su.afk.yummy.tv.feature.main

import su.afk.yummy.tv.core.analytics.api.AnalyticsTracker
import su.afk.yummy.tv.core.analytics.utils.analyticsParamsOf
import su.afk.yummy.tv.core.model.settings.YaniApplicationTokenState
import javax.inject.Inject

internal class MainAnalytics @Inject constructor(
    private val tracker: AnalyticsTracker,
) {
    /**
     * Пользователь открыл главный экран приложения.
     */
    fun eventScreenOpened() {
        tracker.track(EVENT_SCREEN_OPENED)
    }

    /**
     * Сессия приложения после разрешения состояния авторизации.
     *
     * Параметры: auth_state, yani_application_token_state.
     */
    fun eventAppSession(
        isAuthorized: Boolean,
        yaniApplicationTokenState: YaniApplicationTokenState,
    ) {
        tracker.track(
            EVENT_APP_SESSION,
            analyticsParamsOf(
                PARAM_AUTH_STATE to isAuthorized.authStateValue(),
                PARAM_YANI_APPLICATION_TOKEN_STATE to yaniApplicationTokenState.analyticsValue(),
            ),
        )
    }

    private fun Boolean.authStateValue(): String =
        if (this) AUTH_STATE_AUTHORIZED else AUTH_STATE_ANONYMOUS

    private fun YaniApplicationTokenState.analyticsValue(): String =
        when (this) {
            YaniApplicationTokenState.DEFAULT -> YANI_APPLICATION_TOKEN_STATE_DEFAULT
            YaniApplicationTokenState.CUSTOM -> YANI_APPLICATION_TOKEN_STATE_CUSTOM
        }

    private companion object {
        const val EVENT_SCREEN_OPENED = "main_screen"
        const val EVENT_APP_SESSION = "app_session"
        const val PARAM_AUTH_STATE = "auth_state"
        const val PARAM_YANI_APPLICATION_TOKEN_STATE = "yani_application_token_state"
        const val AUTH_STATE_AUTHORIZED = "authorized"
        const val AUTH_STATE_ANONYMOUS = "anonymous"
        const val YANI_APPLICATION_TOKEN_STATE_DEFAULT = "default"
        const val YANI_APPLICATION_TOKEN_STATE_CUSTOM = "custom"
    }
}
