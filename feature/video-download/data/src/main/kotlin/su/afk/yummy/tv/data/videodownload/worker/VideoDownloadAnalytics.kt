package su.afk.yummy.tv.data.videodownload.worker

import su.afk.yummy.tv.core.analytics.AnalyticsTracker
import su.afk.yummy.tv.core.analytics.analyticsParamsOf
import su.afk.yummy.tv.domain.videodownload.model.VideoDownloadItem
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VideoDownloadAnalytics @Inject constructor(
    private val tracker: AnalyticsTracker,
) {
    // WorkManager runs each retry (automatic or manual) as a fresh Worker, so a source that stays
    // broken calls reportFailed again on every attempt. Singleton scope lets this set survive
    // across those attempts: only the first failure per download id is reported, and a later
    // success clears it so a fresh failure on that id can report again.
    private val reportedFailureIds = ConcurrentHashMap.newKeySet<Long>()

    /** Пользователь поставил серию в очередь скачивания (или перезапустил упавшую). */
    fun reportEnqueued(item: VideoDownloadItem, restarted: Boolean) {
        tracker.track(
            EVENT_ENQUEUED,
            item.analyticsParams() + analyticsParamsOf(PARAM_RESTARTED to restarted),
        )
    }

    fun reportSucceeded(item: VideoDownloadItem) {
        reportedFailureIds.remove(item.id)
        tracker.track(EVENT_SUCCEEDED, item.analyticsParams())
    }

    fun reportFailed(
        item: VideoDownloadItem,
        details: String,
        throwable: Throwable? = null,
    ) {
        if (!reportedFailureIds.add(item.id)) return
        val message = buildString {
            append("Video download failed (")
            append(item.analyticsParams().entries.joinToString { (key, value) -> "$key=$value" })
            append("): ")
            append(details)
        }
        tracker.reportError(
            message = message,
            throwable = throwable ?: IllegalStateException(details),
            groupIdentifier = ERROR_GROUP,
        )
    }

    private fun VideoDownloadItem.analyticsParams(): Map<String, String> = analyticsParamsOf(
        PARAM_DOWNLOAD_ID to id,
        PARAM_ANIME_ID to animeId,
        PARAM_ANIME_TITLE to animeTitle,
        PARAM_EPISODE to episode,
        PARAM_DUBBING to dubbing,
        PARAM_PLAYER to playerName,
        PARAM_PLAYER_ID to playerId,
        PARAM_QUALITY to qualityLabel,
    )

    private companion object {
        const val EVENT_ENQUEUED = "video_download_enqueued"
        const val EVENT_SUCCEEDED = "video_download_succeeded"
        const val PARAM_RESTARTED = "restarted"
        const val ERROR_GROUP = "video_download_failed"
        const val PARAM_DOWNLOAD_ID = "download_id"
        const val PARAM_ANIME_ID = "anime_id"
        const val PARAM_ANIME_TITLE = "anime_title"
        const val PARAM_EPISODE = "episode"
        const val PARAM_DUBBING = "dubbing"
        const val PARAM_PLAYER = "player"
        const val PARAM_PLAYER_ID = "player_id"
        const val PARAM_QUALITY = "quality"
    }
}
