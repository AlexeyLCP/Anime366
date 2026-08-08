package su.afk.yummy.tv.feature.account.account.model

/** Notification just marked read: waiting for its `viewed` flag to flip so focus can move to Delete. */
internal data class PendingNotificationReadFocus(
    val notificationId: Int,
    val sawLoading: Boolean = false,
)
