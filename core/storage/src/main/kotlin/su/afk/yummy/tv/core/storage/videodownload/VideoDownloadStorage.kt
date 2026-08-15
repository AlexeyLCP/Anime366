package su.afk.yummy.tv.core.storage.videodownload

import kotlinx.coroutines.flow.Flow

/** Абстракция над локальным хранилищем скачанных видео — позволяет подменять реализацию в тестах. */
interface VideoDownloadStorage {

    fun observeDownloads(): Flow<List<VideoDownloadEntry>>

    fun observeDownloadsForAnime(animeId: Int): Flow<List<VideoDownloadEntry>>

    suspend fun getById(id: Long): VideoDownloadEntry?

    suspend fun getUnfinishedDownloads(): List<VideoDownloadEntry>

    suspend fun getUnfinishedExports(): List<VideoDownloadEntry>

    suspend fun findEpisodeDownload(animeId: Int, episode: String): VideoDownloadEntry?

    suspend fun getEpisodeDownloads(animeId: Int, episode: String): List<VideoDownloadEntry>

    suspend fun markEpisodeDeleted(animeId: Int, episode: String, updatedAt: Long)

    suspend fun markOtherFailedEpisodeDownloadsDeleted(
        animeId: Int,
        episode: String,
        keepId: Long,
        updatedAt: Long,
    )

    suspend fun getActiveCacheKeys(): List<String>

    suspend fun insert(entry: VideoDownloadEntry): Long

    suspend fun update(entry: VideoDownloadEntry)
}
