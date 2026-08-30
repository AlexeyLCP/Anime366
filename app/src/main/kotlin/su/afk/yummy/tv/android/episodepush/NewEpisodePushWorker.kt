package su.afk.yummy.tv.android.episodepush

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import su.afk.yummy.tv.core.preferences.settings.EpisodePushSettingsStore
import su.afk.yummy.tv.core.preferences.settings.YaniAccountSettingsStore
import su.afk.yummy.tv.domain.account.model.ProfileNotification
import su.afk.yummy.tv.domain.account.usecase.GetProfileNotificationsUseCase
import su.afk.yummy.tv.domain.account.usecase.ResolveNotificationAnimeIdUseCase

/**
 * Раз в [su.afk.yummy.tv.android.episodepush.NewEpisodePushScheduler] опрашивает ту же ленту
 * уведомлений, что рисует вкладка «Уведомления» в аккаунте, и превращает новые
 * `isNewEpisode`-записи в системные пуши — даже если приложение не открывали. Сервер сам решает,
 * что считать новой серией по подписке ([ProfileNotification.isNewEpisode]), поэтому воркеру не
 * нужно ничего диффать самостоятельно.
 */
@HiltWorker
class NewEpisodePushWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val accountSettingsStore: YaniAccountSettingsStore,
    private val episodePushSettingsStore: EpisodePushSettingsStore,
    private val getProfileNotifications: GetProfileNotificationsUseCase,
    private val resolveNotificationAnimeId: ResolveNotificationAnimeIdUseCase,
    private val notificationService: NewEpisodePushNotificationService,
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        if (!episodePushSettingsStore.pushEnabled.first()) return Result.success()
        val userId = accountSettingsStore.yaniUserId.first()
        if (userId <= 0) return Result.success()

        return runCatching {
            val newEpisodeNotifications = getProfileNotifications(limit = NOTIFICATIONS_PAGE_SIZE)
                .filter { it.isNewEpisode }
            val knownIds = episodePushSettingsStore.knownNotificationIds.first()

            if (knownIds.isEmpty()) {
                // Первый прогон после включения тумблера: фиксируем текущее состояние ленты как
                // базовую линию, чтобы не засыпать пользователя пушами про уже вышедшие серии.
                episodePushSettingsStore.addKnownNotificationIds(
                    newEpisodeNotifications.map(ProfileNotification::id).toSet(),
                )
                return@runCatching
            }

            val newOnes = newEpisodeNotifications.filterNot { it.id in knownIds }
            newOnes.forEach { notification -> pushNotification(notification) }
            episodePushSettingsStore.addKnownNotificationIds(
                newOnes.map(ProfileNotification::id).toSet()
            )
        }.fold(
            onSuccess = { Result.success() },
            onFailure = { Result.retry() },
        )
    }

    private suspend fun pushNotification(notification: ProfileNotification) {
        val animeId = notification.animeSlug?.let { slug -> resolveNotificationAnimeId(slug) }
        notificationService.showNewEpisode(notification, animeId)
    }

    private companion object {
        const val NOTIFICATIONS_PAGE_SIZE = 30
    }
}
