package su.afk.yummy.tv.data.library.repository

import su.afk.yummy.tv.data.library.mapper.toDomainOrNull
import su.afk.yummy.tv.data.library.network.YaniWatchHistoryApi
import su.afk.yummy.tv.domain.library.model.WatchHistoryEntry
import su.afk.yummy.tv.domain.library.repository.WatchHistoryRepository
import javax.inject.Inject

class YaniWatchHistoryRepository @Inject constructor(
    private val api: YaniWatchHistoryApi,
) : WatchHistoryRepository {
    override suspend fun getPage(limit: Int, offset: Int): List<WatchHistoryEntry> =
        api.getPage(limit, offset).response.mapNotNull { it.toDomainOrNull() }
}
