package su.afk.yummy.tv.domain.account.usecase

import su.afk.yummy.tv.domain.account.model.SubscriptionKeys
import su.afk.yummy.tv.domain.account.model.VideoSubscriptionSelection
import su.afk.yummy.tv.domain.account.mutation.AccountMutationAction
import su.afk.yummy.tv.domain.account.mutation.AccountMutationErrorNotifier
import su.afk.yummy.tv.domain.account.repository.VideoSubscriptionRepository
import javax.inject.Inject

/**
 * Переключает подписку на обновления выбранной озвучки.
 *
 * Сервер принимает только `video_id`, а в ответе списка подписок озвучку не возвращает, поэтому
 * выбранная озвучка и использованный `video_id` сохраняются локально — иначе отписаться ровно от неё
 * будет нечем.
 */
class SetVideoSubscriptionUseCase @Inject constructor(
    private val repository: VideoSubscriptionRepository,
    private val mutationErrorNotifier: AccountMutationErrorNotifier,
) {
    suspend operator fun invoke(
        userId: Int,
        animeId: Int,
        playerId: Int?,
        player: String,
        dubbing: String,
        videoId: Int,
        subscribed: Boolean,
    ): Boolean {
        val changed = notifyBooleanMutationFailure(
            mutationErrorNotifier,
            if (subscribed) {
                AccountMutationAction.SET_VIDEO_SUBSCRIPTION
            } else {
                AccountMutationAction.REMOVE_VIDEO_SUBSCRIPTION
            },
        ) {
            repository.setSubscribed(videoId, subscribed)
        }
        if (!changed) return false

        val playerKey = SubscriptionKeys.playerKey(playerId, player)
        val dubbingKey = SubscriptionKeys.dubbingKey(dubbing)
        if (subscribed) {
            repository.saveSelection(
                userId = userId,
                selection = VideoSubscriptionSelection(
                    animeId = animeId,
                    playerKey = playerKey,
                    dubbingKey = dubbingKey,
                    videoId = videoId,
                    updatedAt = System.currentTimeMillis(),
                ),
            )
        } else {
            repository.removeSelection(userId, animeId, playerKey, dubbingKey)
        }
        return true
    }
}
