package su.afk.yummy.tv.data.account.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import su.afk.yummy.tv.core.preferences.settings.YaniAccountSettingsStore
import su.afk.yummy.tv.core.preferences.settings.currentLanguageCode
import su.afk.yummy.tv.core.storage.account.AccountStorage
import su.afk.yummy.tv.core.storage.account.AccountVideoSubscriptionsCache
import su.afk.yummy.tv.core.storage.account.isFresh
import su.afk.yummy.tv.core.storage.offlinefirst.offlineFirstCache
import su.afk.yummy.tv.core.storage.subscriptionselection.VideoSubscriptionSelectionEntry
import su.afk.yummy.tv.core.storage.subscriptionselection.VideoSubscriptionSelectionStorage
import su.afk.yummy.tv.data.account.network.YaniAccountApi
import su.afk.yummy.tv.data.account.storage.mapper.toVideoSubscriptions
import su.afk.yummy.tv.data.account.storage.mapper.toVideoSubscriptionsCache
import su.afk.yummy.tv.domain.account.model.VideoSubscription
import su.afk.yummy.tv.domain.account.model.VideoSubscriptionSelection
import su.afk.yummy.tv.domain.account.repository.VideoSubscriptionRepository

class YaniVideoSubscriptionRepository(
    private val api: YaniAccountApi,
    private val accountStorage: AccountStorage,
    private val selectionStorage: VideoSubscriptionSelectionStorage,
    private val settingsStore: YaniAccountSettingsStore,
) : VideoSubscriptionRepository {

    override suspend fun getSubscriptions(userId: Int): List<VideoSubscription> =
        withContext(Dispatchers.IO) {
            val languageCode = settingsStore.currentLanguageCode()
            offlineFirstCache(
                read = { accountStorage.getVideoSubscriptions(userId, languageCode) },
                isFresh = { it.isFresh(ACCOUNT_SHORT_TTL_MS) },
                toDomain = { it.toVideoSubscriptions() },
                fetchAndSave = { fetchSubscriptions(userId, languageCode) },
            )
        }

    override suspend fun setSubscribed(videoId: Int, subscribed: Boolean): Boolean =
        withContext(Dispatchers.IO) {
            val userId = settingsStore.yaniUserId.first()
            val result = if (subscribed) {
                api.setSubscribed(videoId)
            } else {
                api.removeSubscribed(videoId)
            }
            if (userId > 0) {
                accountStorage.deleteVideoSubscriptions(userId)
            }
            result
        }

    override suspend fun getSelections(
        userId: Int,
        animeId: Int,
    ): List<VideoSubscriptionSelection> = withContext(Dispatchers.IO) {
        selectionStorage.getForAnime(userId, animeId).map { entry ->
            VideoSubscriptionSelection(
                animeId = entry.animeId,
                playerKey = entry.playerKey,
                dubbingKey = entry.dubbingKey,
                videoId = entry.videoId,
                updatedAt = entry.updatedAt,
            )
        }
    }

    override suspend fun saveSelection(userId: Int, selection: VideoSubscriptionSelection) =
        withContext(Dispatchers.IO) {
            selectionStorage.save(
                VideoSubscriptionSelectionEntry(
                    userId = userId,
                    animeId = selection.animeId,
                    playerKey = selection.playerKey,
                    dubbingKey = selection.dubbingKey,
                    videoId = selection.videoId,
                    updatedAt = selection.updatedAt,
                )
            )
        }

    override suspend fun removeSelection(
        userId: Int,
        animeId: Int,
        playerKey: String,
        dubbingKey: String,
    ) = withContext(Dispatchers.IO) {
        selectionStorage.delete(userId, animeId, playerKey, dubbingKey)
    }

    override suspend fun removeSelectionsForPlayer(
        userId: Int,
        animeId: Int,
        playerKey: String,
    ) = withContext(Dispatchers.IO) {
        selectionStorage.deleteForPlayer(userId, animeId, playerKey)
    }

    private suspend fun fetchSubscriptions(
        userId: Int,
        languageCode: String,
    ): AccountVideoSubscriptionsCache {
        val cache = api.getSubscriptions(userId).toVideoSubscriptionsCache(
            userId = userId,
            language = languageCode,
            cachedAt = System.currentTimeMillis(),
        )
        accountStorage.saveVideoSubscriptions(cache)
        return cache
    }
}
