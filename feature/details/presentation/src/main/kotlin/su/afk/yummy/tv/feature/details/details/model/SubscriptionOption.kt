package su.afk.yummy.tv.feature.details.details.model

data class SubscriptionOption(
    /** [su.afk.yummy.tv.domain.account.model.SubscriptionKeys.subscriptionKey] озвучки и балансера. */
    val key: String,
    val playerId: Int?,
    val player: String,
    val dubbing: String,
    val episodesCount: Int,
    /** id видео для запроса подписки: сохранённый при подписке, иначе последняя серия озвучки. */
    val subscriptionVideoId: Int,
    val isSubscribed: Boolean,
)
