package su.afk.yummy.tv.data.library.network

import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import kotlinx.coroutines.flow.first
import su.afk.yummy.tv.core.network.YANI_BASE_URL
import su.afk.yummy.tv.core.network.YaniHttpClientProvider
import su.afk.yummy.tv.core.network.getOrFetchJson
import su.afk.yummy.tv.core.preferences.settings.SettingsStore
import su.afk.yummy.tv.core.storage.document.DocumentCacheStore
import su.afk.yummy.tv.data.library.dto.YaniWatchHistoryResponseDto
import javax.inject.Inject

class YaniWatchHistoryApi @Inject constructor(
    private val clientProvider: YaniHttpClientProvider,
    private val documentCache: DocumentCacheStore,
    private val settingsStore: SettingsStore,
) {
    suspend fun getPage(limit: Int, offset: Int): YaniWatchHistoryResponseDto {
        val userId = settingsStore.yaniUserId.first()
        return documentCache.getOrFetchJson(
            cacheKey = "watch-history:$userId:$limit:$offset",
            ttlMs = CACHE_TTL_MS,
        ) {
            clientProvider.get().get("$YANI_BASE_URL/video/watch-history") {
                parameter("limit", limit)
                parameter("offset", offset)
            }.body()
        }
    }

    private companion object {
        const val CACHE_TTL_MS = 10_000L
    }
}
