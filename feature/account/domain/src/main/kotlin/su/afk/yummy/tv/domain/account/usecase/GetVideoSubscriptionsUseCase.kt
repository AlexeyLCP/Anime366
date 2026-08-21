package su.afk.yummy.tv.domain.account.usecase

import su.afk.yummy.tv.domain.account.model.VideoSubscription
import su.afk.yummy.tv.domain.account.repository.VideoSubscriptionRepository
import javax.inject.Inject

/** Загружает список подписок пользователя для экрана «Мои подписки». */
class GetVideoSubscriptionsUseCase @Inject constructor(
    private val repository: VideoSubscriptionRepository,
) {
    suspend operator fun invoke(userId: Int): List<VideoSubscription> =
        repository.getSubscriptions(userId)
}
