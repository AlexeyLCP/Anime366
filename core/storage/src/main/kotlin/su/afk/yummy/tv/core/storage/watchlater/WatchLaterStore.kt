package su.afk.yummy.tv.core.storage.watchlater

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import su.afk.yummy.tv.core.storage.watchprogress.WatchProgressDao
import su.afk.yummy.tv.core.storage.watchprogress.WatchProgressEntry
import su.afk.yummy.tv.core.storage.watchprogress.WatchProgressStore.Companion.MIN_CONTINUE_WATCHING_POSITION_MS
import su.afk.yummy.tv.core.storage.watchprogress.WatchProgressStore.Companion.isWatchedProgressEntry

/**
 * Пометка «отложено» снимается сама, как только серия досмотрена, поэтому список всегда
 * сверяется с прогрессом просмотра. Правило «просмотрено» нетривиальное (пороги живут в
 * `core:model`), поэтому фильтруем в Kotlin, а не в SQL. Прогресс с сервера кешируется в
 * ту же таблицу, так что досмотр на другом устройстве тоже учитывается.
 */
internal class WatchLaterStore(
    private val dao: WatchLaterDao,
    private val watchProgressDao: WatchProgressDao,
) : WatchLaterStorage {

    override fun observeAll(): Flow<List<WatchLaterEntry>> =
        combine(dao.observeAll(), watchProgressDao.observeAll()) { entries, progress ->
            entries.filterNot { it.isWatched(progress) }
        }

    override fun observeByAnimeId(animeId: Int): Flow<List<WatchLaterEntry>> =
        combine(
            dao.observeByAnimeId(animeId),
            watchProgressDao.observeByAnimeId(animeId),
        ) { entries, progress ->
            entries.filterNot { it.isWatched(progress) }
        }

    override suspend fun save(
        animeId: Int,
        episode: String,
        animeTitle: String,
        posterUrl: String,
        screenshotUrl: String,
        addedAt: Long,
    ) {
        dao.save(
            WatchLaterEntry(
                animeId = animeId,
                episode = episode,
                animeTitle = animeTitle,
                posterUrl = posterUrl,
                screenshotUrl = screenshotUrl,
                addedAt = addedAt,
            )
        )
    }

    override suspend fun delete(animeId: Int, episode: String) = dao.delete(animeId, episode)

    override suspend fun deleteByAnimeId(animeId: Int) = dao.deleteByAnimeId(animeId)

    override suspend fun pruneWatched() {
        val progress = watchProgressDao.watchedProgress(MIN_CONTINUE_WATCHING_POSITION_MS)
        dao.all()
            .filter { it.isWatched(progress) }
            .forEach { dao.delete(it.animeId, it.episode) }
    }
}

/**
 * Номер серии в прогрессе может отличаться ведущим нулём от сохранённого ключа, поэтому
 * сравниваем нормализованные значения.
 */
private fun WatchLaterEntry.isWatched(progress: List<WatchProgressEntry>): Boolean =
    progress.any { entry ->
        entry.animeId == animeId &&
                entry.episode.watchLaterEpisodeKey() == episode.watchLaterEpisodeKey() &&
                isWatchedProgressEntry(entry)
    }

/**
 * Локальная копия `episodeGroupKey` из `core:utils`: `core:storage` от него не зависит,
 * а тянуть модуль ради одной строки не хочется.
 */
internal fun String.watchLaterEpisodeKey(): String =
    trim().trimStart('0').ifEmpty { trim() }.lowercase()
