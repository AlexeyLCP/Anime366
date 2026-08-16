package su.afk.yummy.tv.data.home.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import su.afk.yummy.tv.core.analytics.api.AnalyticsTracker
import su.afk.yummy.tv.core.error.api.StringProvider
import su.afk.yummy.tv.core.preferences.settings.YaniAccountSettingsStore
import su.afk.yummy.tv.core.storage.home.HomeFeedCache
import su.afk.yummy.tv.core.storage.home.HomeFeedStorage
import su.afk.yummy.tv.core.storage.home.isFresh
import su.afk.yummy.tv.core.storage.offlinefirst.offlineFirstCache
import su.afk.yummy.tv.core.storage.watchprogress.WatchProgressEntry
import su.afk.yummy.tv.core.storage.watchprogress.WatchProgressStorage
import su.afk.yummy.tv.core.utils.network.isLikelyImageUrl
import su.afk.yummy.tv.data.home.dto.YaniFeedDto
import su.afk.yummy.tv.data.home.dto.YaniVideoDto
import su.afk.yummy.tv.data.home.network.YaniHomeApi
import su.afk.yummy.tv.data.home.storage.mapper.toHomeContinueWatchingItem
import su.afk.yummy.tv.data.home.storage.mapper.toHomeFeedCache
import su.afk.yummy.tv.domain.home.model.ContinueWatchingProgressMigration
import su.afk.yummy.tv.domain.home.model.HomeContinueWatchingItem
import su.afk.yummy.tv.domain.home.model.HomeFeed
import su.afk.yummy.tv.domain.home.model.HomeFeedSectionType
import su.afk.yummy.tv.domain.home.repository.HomeFeedRepository
import su.afk.yummy.tv.data.home.storage.mapper.toHomeFeed as toStoredHomeFeed

private const val FEED_TTL_MS = 60 * 1000L
private const val FEED_CACHE_SIGNATURE_VERSION = "cw-local1"
private const val TAG = "YaniHomeFeed"

class YaniHomeFeedRepository(
    private val api: YaniHomeApi,
    private val homeFeedStore: HomeFeedStorage,
    private val stringProvider: StringProvider,
    private val settingsStore: YaniAccountSettingsStore,
    private val watchProgressStore: WatchProgressStorage,
    private val analyticsTracker: AnalyticsTracker,
) : HomeFeedRepository {

    override suspend fun getHomeFeed(): HomeFeed = getHomeFeed(forceRefresh = false)

    override suspend fun getCachedHomeFeed(): HomeFeed? = withContext(Dispatchers.IO) {
        val languageCode = settingsStore.yaniContentLanguage.first().apiCode
        val watchSignature = feedCacheSignature()
        val displayWatchEntries = displayWatchEntries()
        val hiddenIds = hiddenRecommendationIds()
        homeFeedStore.getFeed(languageCode, watchSignature)
            ?.toStoredHomeFeed(stringProvider)
            ?.withLocalOverrides(displayWatchEntries, hiddenIds)
    }

    override suspend fun refreshHomeFeed(): HomeFeed = getHomeFeed(forceRefresh = true)

    override suspend fun removeCachedContinueWatching(animeId: Int) {
        withContext(Dispatchers.IO) {
            watchProgressStore.suppressContinueWatchingDisplay(animeId)
            homeFeedStore.deleteContinueWatchingByAnimeId(animeId)
        }
    }

    override suspend fun getContinueWatchingVideoIds(animeId: Int): List<Int> =
        withContext(Dispatchers.IO) {
            watchProgressStore.continueWatching()
                .filter { it.animeId == animeId }
                .map { it.videoId }
                .filter { it > 0 }
                .distinct()
        }

    override suspend fun migrateContinueWatchingProgress(
        migration: ContinueWatchingProgressMigration,
    ) = withContext(Dispatchers.IO) {
        watchProgressStore.save(
            animeId = migration.animeId,
            episode = migration.episode,
            videoId = migration.videoId,
            episodeUrl = migration.episodeUrl,
            positionMs = migration.positionMs,
            durationMs = migration.durationMs,
            animeTitle = migration.animeTitle,
            posterUrl = migration.posterUrl,
            playerName = migration.playerName,
            dubbing = migration.dubbing,
            screenshotUrl = migration.screenshotUrl,
        )
        watchProgressStore.delete(migration.animeId, migration.previousEpisode)
    }

    override fun observeContinueWatching(): Flow<List<HomeContinueWatchingItem>> =
        watchProgressStore.observeContinueWatching()
            .map(::localContinueWatchingItems)
            .distinctUntilChanged()

    private suspend fun getHomeFeed(forceRefresh: Boolean): HomeFeed = withContext(Dispatchers.IO) {
        val languageCode = settingsStore.yaniContentLanguage.first().apiCode
        val watchSignature = feedCacheSignature()
        val displayWatchEntries = displayWatchEntries()
        // Единый снимок на весь вызов: используется во всех трёх ветках (свежий кэш, сеть,
        // fallback при ошибке), чтобы не пересчитывать его отдельно для сетевой ветки.
        val hiddenIds = hiddenRecommendationIds()
        offlineFirstCache(
            forceRefresh = forceRefresh,
            read = { homeFeedStore.getFeed(languageCode, watchSignature) },
            isFresh = { it.isFresh(FEED_TTL_MS) },
            toDomain = { it.toStoredHomeFeed(stringProvider) },
            fetchAndSave = { fetchHomeFeed(languageCode, watchSignature) },
            transform = { it.withLocalOverrides(displayWatchEntries, hiddenIds) },
        )
    }

    private suspend fun fetchHomeFeed(
        languageCode: String,
        watchSignature: String,
    ): HomeFeedCache {
        analyticsTracker.log(TAG) { "Fetch feed language=$languageCode watchSignature=$watchSignature" }
        val dto = api.getFeed()
        analyticsTracker.log(TAG) { "Feed dto ${dto.summaryForLog()}" }
        val cache = dto.toHomeFeedCache(
            language = languageCode,
            watchSignature = watchSignature,
            cachedAt = System.currentTimeMillis(),
        )
        homeFeedStore.saveFeed(cache)
        analyticsTracker.log(TAG) {
            val feed = cache.toStoredHomeFeed(stringProvider)
            "Feed mapped ${feed.summaryForLog()} " +
                    "continueSamples=${feed.continueWatchingItems.summaryForLog()}"
        }
        return cache
    }

    private suspend fun displayWatchEntries(): List<WatchProgressEntry> =
        watchProgressStore.continueWatching()

    private suspend fun hiddenRecommendationIds(): Set<Int> =
        settingsStore.hiddenRecommendationIds.first()

    // Применяется одинаково к результату из кэша, из сети и к fallback при ошибке: "продолжить
    // просмотр" всегда пересчитывается из актуального локального прогресса, а не из момента
    // кэширования фида, а скрытые пользователем рекомендации отфильтровываются до ближайшего
    // пересчёта рекомендаций на бэкенде.
    private fun HomeFeed.withLocalOverrides(
        localEntries: List<WatchProgressEntry>,
        hiddenIds: Set<Int>,
    ): HomeFeed = copy(
        continueWatchingItems = localContinueWatchingItems(localEntries),
        sections = if (hiddenIds.isEmpty()) {
            sections
        } else {
            sections.map { section ->
                if (section.type == HomeFeedSectionType.RECOMMENDATIONS) {
                    section.copy(items = section.items.filterNot { it.id in hiddenIds })
                } else {
                    section
                }
            }
        },
    )

    private fun feedCacheSignature(): String = FEED_CACHE_SIGNATURE_VERSION

    private fun localContinueWatchingItems(
        entries: List<WatchProgressEntry>,
    ): List<HomeContinueWatchingItem> =
        entries
            .filter { it.animeId > 0 }
            .groupBy { it.animeId }
            .values
            .mapNotNull { group ->
                group.maxWithOrNull(
                    compareBy<WatchProgressEntry> { it.updatedAt }
                        .thenBy { it.positionMs }
                        .thenBy { it.videoId }
                        .thenBy { it.episode }
                )
            }
            .sortedByDescending { it.updatedAt }
            .map { it.toHomeContinueWatchingItem() }

    private fun YaniFeedDto.summaryForLog(): String {
        val data = response
        return buildString {
            append("announcements=${data.announcements.size}")
            append(" topCarousel=${data.topCarousel.items.size}")
            append(" new=${data.new.size}")
            append(" recommends=${data.recommends.size}")
            append(" newVideos=${data.newVideos.size}")
            append(" schedule=${data.schedule.size}")
            append(" posts=${data.posts.items.size}")
            append(" bloggerVideos=${data.blogger.videos.items.size}")
            append(" collections=${data.collections.size}")
            append(" newVideoSamples=")
            append(data.newVideos.take(LOG_SAMPLE_LIMIT).joinToString(prefix = "[", postfix = "]") {
                it.summaryForLog()
            })
        }
    }

    private fun YaniVideoDto.summaryForLog(): String =
        "video=$videoId anime=$animeId title=${title.safeForLog()} " +
                "episode=${episodeTitle.safeForLog()} dub=${dubTitle.safeForLog()} " +
                "player=${playerTitle.safeForLog()}"

    private fun HomeFeed.summaryForLog(): String =
        "continueWatching=${continueWatchingItems.size} hero=${heroItems.size} sections=" +
                sections.joinToString(prefix = "[", postfix = "]") { section ->
                    "${section.type}:${section.items.size}"
                }

    private fun List<HomeContinueWatchingItem>.summaryForLog(): String =
        take(LOG_SAMPLE_LIMIT).joinToString(prefix = "[", postfix = "]") {
            "anime=${it.animeId} episode=${it.episode.safeForLog()} video=${it.videoId} " +
                    "durationMs=${it.durationMs} screenshot=${it.screenshotSourceForLog()}"
        }

    private fun HomeContinueWatchingItem.screenshotSourceForLog(): String =
        when {
            screenshotUrl.isBlank() -> "none"
            screenshotUrl.contains("kodik", ignoreCase = true) -> "kodik"
            screenshotUrl.isLikelyImageUrl() -> "direct"
            else -> "source"
        }

    private fun String?.safeForLog(): String =
        this
            ?.lineSequence()
            ?.joinToString(" ")
            ?.take(LOG_TEXT_LIMIT)
            ?: "null"

    private companion object {
        const val LOG_SAMPLE_LIMIT = 8
        const val LOG_TEXT_LIMIT = 80
    }
}
