package su.afk.yummy.tv.domain.account.usecase

import su.afk.yummy.tv.domain.account.model.AnimeSubscriptionState
import su.afk.yummy.tv.domain.account.model.SubscriptionKeys
import su.afk.yummy.tv.domain.account.repository.VideoSubscriptionRepository
import javax.inject.Inject

/**
 * Сверяет локально сохранённые подписки тайтла со списком на сервере.
 *
 * `/users/{id}/lists/subs` отдаёт по строке на подписку, но озвучку в ней не сообщает: `sub.dubbing`
 * приходит либо пустым, либо перечислением всех озвучек плеера. Поэтому какая именно озвучка выбрана —
 * знает только локальная запись, а серверный список используется как счётчик: сколько подписок реально
 * есть на каждом балансере тайтла. Лишние локальные записи (отписались с сайта) отбрасываются.
 */
class GetAnimeSubscriptionStateUseCase @Inject constructor(
    private val repository: VideoSubscriptionRepository,
) {
    suspend operator fun invoke(
        userId: Int,
        animeId: Int,
        animeUrl: String? = null,
    ): AnimeSubscriptionState {
        val serverCountByPlayer = repository.getSubscriptions(userId)
            .filter { it.animeId == animeId || (!animeUrl.isNullOrBlank() && it.animeUrl == animeUrl) }
            .groupingBy { SubscriptionKeys.playerKey(it.playerId, it.player) }
            .eachCount()

        val alive = repository.getSelections(userId, animeId)
            .groupBy { it.playerKey }
            .flatMap { (playerKey, selections) ->
                val serverCount = serverCountByPlayer[playerKey] ?: 0
                when {
                    serverCount == 0 -> {
                        repository.removeSelectionsForPlayer(userId, animeId, playerKey)
                        emptyList()
                    }

                    serverCount < selections.size -> {
                        val ordered = selections.sortedByDescending { it.updatedAt }
                        ordered.drop(serverCount).forEach {
                            repository.removeSelection(userId, animeId, playerKey, it.dubbingKey)
                        }
                        ordered.take(serverCount)
                    }

                    else -> selections
                }
            }

        return AnimeSubscriptionState(
            subscribedKeys = alive.map { it.subscriptionKey }.toSet(),
            videoIdsByKey = alive.associate { it.subscriptionKey to it.videoId },
        )
    }
}
