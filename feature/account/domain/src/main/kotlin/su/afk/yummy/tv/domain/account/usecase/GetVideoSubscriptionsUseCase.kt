package su.afk.yummy.tv.domain.account.usecase

import su.afk.yummy.tv.domain.account.model.SubscriptionKeys
import su.afk.yummy.tv.domain.account.model.VideoSubscription
import su.afk.yummy.tv.domain.account.repository.VideoSubscriptionRepository
import javax.inject.Inject

/**
 * Загружает список подписок пользователя для экрана «Мои подписки».
 *
 * `/users/{id}/lists/subs` отдаёт по строке на подписку, а озвучку не сообщает
 * (см. docs/subscriptions.md), поэтому подписки на разные озвучки одного балансера приходят
 * несколькими одинаковыми с точки зрения UI записями. Схлопываем их до одной строки на пару
 * «тайтл + балансер».
 */
class GetVideoSubscriptionsUseCase @Inject constructor(
    private val repository: VideoSubscriptionRepository,
) {
    suspend operator fun invoke(userId: Int): List<VideoSubscription> =
        repository.getSubscriptions(userId)
            .distinctBy { SubscriptionKeys.animePlayerKey(it.animeId, it.playerId, it.player) }
}
