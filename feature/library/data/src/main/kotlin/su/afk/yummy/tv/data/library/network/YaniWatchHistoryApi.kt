package su.afk.yummy.tv.data.library.network

import su.afk.yummy.tv.data.library.dto.YaniWatchHistoryResponseDto
import javax.inject.Inject

class YaniWatchHistoryApi @Inject constructor() {
    suspend fun getPage(limit: Int, offset: Int): YaniWatchHistoryResponseDto =
        YaniWatchHistoryResponseDto()
}
