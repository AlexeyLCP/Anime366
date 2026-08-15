package su.afk.yummy.tv.feature.main.handler

import kotlinx.coroutines.withTimeoutOrNull
import su.afk.yummy.tv.core.featuretoggle.api.VersionSupportChecker
import su.afk.yummy.tv.core.preferences.settings.SettingsStore
import su.afk.yummy.tv.core.update.api.UpdateChecker
import su.afk.yummy.tv.domain.account.usecase.GetAccountSessionUseCase
import su.afk.yummy.tv.domain.account.usecase.GetNotificationCountsUseCase
import su.afk.yummy.tv.domain.account.usecase.RefreshAccountUseCase
import su.afk.yummy.tv.feature.main.utils.firstOrZero
import su.afk.yummy.tv.feature.main.utils.isNewer
import javax.inject.Inject
import javax.inject.Named
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.seconds

/** Checks for app updates, refreshes notification counts and the account session. */
internal class MainSideEffectsHandler @Inject constructor(
    private val updateChecker: UpdateChecker,
    private val versionSupportChecker: VersionSupportChecker,
    private val getNotificationCounts: GetNotificationCountsUseCase,
    private val getAccountSession: GetAccountSessionUseCase,
    private val refreshAccount: RefreshAccountUseCase,
    private val settingsStore: SettingsStore,
    @param:Named("appVersionName") private val versionName: String,
) {
    suspend fun checkForUpdates(): MainUpdateCheckResult =
        runCatching {
            val isCurrentVersionSupported = versionSupportChecker.isCurrentVersionSupported()
            val release = withTimeoutOrNull(GITHUB_UPDATE_TIMEOUT) {
                updateChecker.getLatestRelease()
            } ?: return@runCatching MainUpdateCheckResult.NotAvailable
            val remoteVersion = release.tagName.trimStart('v')
            val apkUrl = release.assets.firstOrNull()?.browserDownloadUrl
                ?: return@runCatching MainUpdateCheckResult.NotAvailable
            if (!isCurrentVersionSupported || isNewer(versionName, remoteVersion)) {
                MainUpdateCheckResult.Available(
                    version = remoteVersion,
                    apkUrl = apkUrl,
                    changelog = release.body.orEmpty(),
                    required = !isCurrentVersionSupported,
                )
            } else {
                MainUpdateCheckResult.NotAvailable
            }
        }.getOrDefault(MainUpdateCheckResult.NotAvailable)

    suspend fun fetchAndPersistNotificationCount(): Boolean =
        runCatching { getNotificationCounts().sumOf { it.count } }
            .onSuccess { count -> settingsStore.setYaniUnreadNotificationsCount(count) }
            .isSuccess

    suspend fun refreshAccountIfStale(maxAge: kotlin.time.Duration = FORTY_EIGHT_HOURS) {
        if (!getAccountSession().isAuthorized) return
        val refreshedAt = settingsStore.yaniTokenRefreshAt.firstOrZero()
        val ageMs = System.currentTimeMillis() - refreshedAt
        if (ageMs > maxAge.inWholeMilliseconds) {
            runCatching { refreshAccount() }
        }
    }

    private companion object {
        val GITHUB_UPDATE_TIMEOUT = 5.seconds
        val FORTY_EIGHT_HOURS = 48.hours
    }
}

/** Outcome of checking GitHub for a newer or non-EOL-supported app version. */
internal sealed interface MainUpdateCheckResult {
    data class Available(
        val version: String,
        val apkUrl: String,
        val changelog: String,
        val required: Boolean,
    ) : MainUpdateCheckResult

    data object NotAvailable : MainUpdateCheckResult
}
