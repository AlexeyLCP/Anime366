package su.afk.yummy.tv.domain.account.repository

import su.afk.yummy.tv.domain.account.model.VideoSubscription
import su.afk.yummy.tv.domain.account.model.VideoSubscriptionSelection

interface VideoSubscriptionRepository {
    suspend fun getSubscriptions(userId: Int): List<VideoSubscription>
    suspend fun setSubscribed(videoId: Int, subscribed: Boolean): Boolean

    /** Локально сохранённые подписки тайтла: какая именно озвучка выбрана. */
    suspend fun getSelections(userId: Int, animeId: Int): List<VideoSubscriptionSelection>

    suspend fun saveSelection(userId: Int, selection: VideoSubscriptionSelection)

    suspend fun removeSelection(userId: Int, animeId: Int, playerKey: String, dubbingKey: String)

    /** Убирает локальные записи плеера, которых больше нет на сервере (отписка с сайта). */
    suspend fun removeSelectionsForPlayer(userId: Int, animeId: Int, playerKey: String)
}
