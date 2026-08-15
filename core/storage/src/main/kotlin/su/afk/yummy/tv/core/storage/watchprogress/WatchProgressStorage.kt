package su.afk.yummy.tv.core.storage.watchprogress

import kotlinx.coroutines.flow.Flow

/** Абстракция над локальным хранилищем прогресса просмотра — позволяет подменять реализацию в тестах. */
interface WatchProgressStorage {

    suspend fun get(animeId: Int, episode: String): WatchProgressEntry?

    fun observeAll(): Flow<List<WatchProgressEntry>>

    fun observeByAnimeId(animeId: Int): Flow<List<WatchProgressEntry>>

    fun observeContinueWatching(): Flow<List<WatchProgressEntry>>

    suspend fun continueWatching(): List<WatchProgressEntry>

    suspend fun latestMeaningfulVideoProgress(limit: Int): List<WatchProgressEntry>

    suspend fun allMeaningfulVideoProgress(): List<WatchProgressEntry>

    suspend fun suppressedContinueWatchingAnimeIds(): Set<Int>

    suspend fun continueWatchingSuppressionTimestamps(): Map<Int, Long>

    fun observeContinueWatchingSuppressionTimestamps(): Flow<Map<Int, Long>>

    suspend fun watchedProgress(): List<WatchProgressEntry>

    fun observeWatchedProgress(): Flow<List<WatchProgressEntry>>

    suspend fun save(
        animeId: Int,
        episode: String,
        videoId: Int = 0,
        episodeUrl: String,
        positionMs: Long,
        durationMs: Long,
        updatedAt: Long = System.currentTimeMillis(),
        animeTitle: String = "",
        posterUrl: String = "",
        playerName: String = "",
        dubbing: String = "",
        screenshotUrl: String = "",
    )

    suspend fun saveContinueTarget(
        animeId: Int,
        episode: String,
        videoId: Int = 0,
        episodeUrl: String,
        updatedAt: Long = System.currentTimeMillis(),
        animeTitle: String = "",
        posterUrl: String = "",
        playerName: String = "",
        dubbing: String = "",
        screenshotUrl: String = "",
    )

    suspend fun delete(animeId: Int, episode: String)

    suspend fun deleteByAnimeId(animeId: Int)

    suspend fun suppressContinueWatching(animeId: Int)

    suspend fun suppressContinueWatchingDisplay(
        animeId: Int,
        suppressedAt: Long = System.currentTimeMillis(),
    )
}
