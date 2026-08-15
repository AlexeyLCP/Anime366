package su.afk.yummy.tv.core.model.anime

private const val MIN_CONTINUE_WATCHING_POSITION_MS = 30_000L
private const val WATCHED_REMAINING_MS = 5 * 60 * 1000L
private const val SHORT_EPISODE_WATCHED_PROGRESS = 0.90f

data class AnimeWatchProgress(
    val animeId: Int,
    val episode: String,
    val videoId: Int = 0,
    val episodeUrl: String,
    val positionMs: Long,
    val durationMs: Long,
    val updatedAt: Long,
    val animeTitle: String = "",
    val posterUrl: String = "",
    val playerName: String = "",
    val dubbing: String = "",
    val screenshotUrl: String = "",
)

/**
 * Порог и функции ниже определяют единственный источник правды для правил
 * "просмотрено" / Continue Watching — переиспользуются как в data-слое (например,
 * [su.afk.yummy.tv.core.storage] через маппинг Entity в [AnimeWatchProgress]), так и
 * напрямую в presentation, когда под рукой есть только сырые positionMs/durationMs.
 */
fun progress(positionMs: Long, durationMs: Long): Float =
    if (durationMs > 0) {
        (positionMs.toFloat() / durationMs.toFloat()).coerceIn(0f, 1f)
    } else {
        0f
    }

fun isMeaningfulProgress(positionMs: Long, durationMs: Long): Boolean =
    durationMs > 0 && positionMs >= MIN_CONTINUE_WATCHING_POSITION_MS

fun isWatchedProgress(positionMs: Long, durationMs: Long): Boolean {
    if (!isMeaningfulProgress(positionMs, durationMs)) return false
    return if (durationMs <= WATCHED_REMAINING_MS) {
        progress(positionMs, durationMs) >= SHORT_EPISODE_WATCHED_PROGRESS
    } else {
        positionMs >= durationMs - WATCHED_REMAINING_MS
    }
}

fun isContinueTarget(positionMs: Long, durationMs: Long): Boolean =
    positionMs == 0L && durationMs == 0L

fun isUnresolvedProgress(positionMs: Long, durationMs: Long): Boolean =
    durationMs == 0L && positionMs >= MIN_CONTINUE_WATCHING_POSITION_MS

fun AnimeWatchProgress.progress(): Float = progress(positionMs, durationMs)

fun AnimeWatchProgress.isMeaningfulProgress(): Boolean =
    isMeaningfulProgress(positionMs, durationMs)

fun AnimeWatchProgress.isContinueTarget(): Boolean =
    isContinueTarget(positionMs, durationMs) &&
            episode.isNotBlank() &&
            episodeUrl.isNotBlank()

fun AnimeWatchProgress.hasPlayableTarget(): Boolean =
    videoId > 0 || episode.isNotBlank() || episodeUrl.isNotBlank()

fun AnimeWatchProgress.isUnresolvedProgress(): Boolean =
    isUnresolvedProgress(positionMs, durationMs) && hasPlayableTarget()

fun AnimeWatchProgress.isWatchedProgress(): Boolean = isWatchedProgress(positionMs, durationMs)

fun AnimeWatchProgress.isContinueWatchingProgress(): Boolean =
    isContinueTarget() ||
            isUnresolvedProgress() ||
            (isMeaningfulProgress() && !isWatchedProgress())
