package su.afk.yummy.tv.android.episodepush

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Планирует [NewEpisodePushWorker] всегда, независимо от состояния тумблера «Пуш о новых
 * сериях» — включение/выключение тумблера гейтит только [NewEpisodePushWorker.doWork], само
 * задание не переэнкьюивается.
 */
@Singleton
class NewEpisodePushScheduler @Inject constructor(
    @param:ApplicationContext private val context: Context,
) {

    fun schedule() {
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            EPISODE_PUSH_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            createPushRequest(),
        )
    }

    private fun createPushRequest() =
        PeriodicWorkRequestBuilder<NewEpisodePushWorker>(
            repeatInterval = EPISODE_PUSH_INTERVAL_HOURS,
            repeatIntervalTimeUnit = TimeUnit.HOURS,
        )
            .setConstraints(networkConstraints())
            .setBackoffCriteria(
                backoffPolicy = BackoffPolicy.EXPONENTIAL,
                backoffDelay = EPISODE_PUSH_BACKOFF_MINUTES,
                timeUnit = TimeUnit.MINUTES,
            )
            .build()

    private fun networkConstraints() =
        Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

    private companion object {
        const val EPISODE_PUSH_WORK_NAME = "new_episode_push"
        const val EPISODE_PUSH_INTERVAL_HOURS = 6L
        const val EPISODE_PUSH_BACKOFF_MINUTES = 30L
    }
}
