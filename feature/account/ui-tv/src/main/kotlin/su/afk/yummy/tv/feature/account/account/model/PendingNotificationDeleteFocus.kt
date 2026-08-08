package su.afk.yummy.tv.feature.account.account.model

/** Notification just deleted: waiting for it to leave the list so focus can land on [fallbackIndex]. */
internal data class PendingNotificationDeleteFocus(
    val notificationId: Int,
    val fallbackIndex: Int,
    val sawLoading: Boolean = false,
)
