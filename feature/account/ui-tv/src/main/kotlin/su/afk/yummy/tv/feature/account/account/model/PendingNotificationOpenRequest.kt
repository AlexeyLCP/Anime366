package su.afk.yummy.tv.feature.account.account.model

/** In-flight "open this notification" flow: resolve anime id, navigate, then restore focus here. */
internal data class PendingNotificationOpenRequest(
    val notificationId: Int,
    val awaitingReturn: Boolean = false,
)
