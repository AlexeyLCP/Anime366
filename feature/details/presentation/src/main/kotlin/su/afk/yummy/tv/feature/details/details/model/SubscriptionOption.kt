package su.afk.yummy.tv.feature.details.details.model

data class SubscriptionOption(
    val key: String,
    val playerId: Int?,
    val player: String,
    val dubbing: String,
    val episodesCount: Int,
    val representativeVideoId: Int,
    val isSubscribed: Boolean,
)
