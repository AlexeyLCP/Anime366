package su.afk.yummy.tv.domain.account.model

/**
 * Состояние подписок тайтла, сверенное с сервером.
 *
 * @param subscribedKeys ключи [SubscriptionKeys.subscriptionKey] озвучек, на которые оформлена подписка.
 * @param videoIdsByKey id видео, которым подписка была создана, — им же нужно отписываться.
 */
data class AnimeSubscriptionState(
    val subscribedKeys: Set<String> = emptySet(),
    val videoIdsByKey: Map<String, Int> = emptyMap(),
)
