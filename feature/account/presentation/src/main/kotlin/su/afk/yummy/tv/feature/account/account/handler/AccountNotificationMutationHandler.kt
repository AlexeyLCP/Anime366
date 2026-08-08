package su.afk.yummy.tv.feature.account.account.handler

import su.afk.yummy.tv.feature.account.account.model.AccountUiError
import javax.inject.Inject

internal class AccountNotificationMutationHandler @Inject constructor(
    private val notificationHandler: AccountNotificationHandler,
) {
    suspend fun markNotificationRead(id: Int): AccountNotificationMutationOutcome =
        mutate(
            error = AccountUiError.UPDATE_NOTIFICATION_FAILED,
            action = { notificationHandler.markNotificationRead(id) },
        )

    suspend fun deleteNotification(id: Int): AccountNotificationMutationOutcome =
        mutate(
            error = AccountUiError.UPDATE_NOTIFICATION_FAILED,
            action = { notificationHandler.deleteNotification(id) },
        )

    suspend fun deleteAllNotifications(): AccountNotificationMutationOutcome =
        mutate(
            error = AccountUiError.UPDATE_NOTIFICATIONS_FAILED,
            action = notificationHandler::deleteAllNotifications,
        )

    suspend fun markAllNotificationsRead(): AccountNotificationMutationOutcome =
        mutate(
            error = AccountUiError.UPDATE_NOTIFICATIONS_FAILED,
            action = notificationHandler::markAllNotificationsRead,
        )

    private suspend fun mutate(
        error: AccountUiError,
        action: suspend () -> Result<Boolean>,
    ): AccountNotificationMutationOutcome =
        action().fold(
            onSuccess = { updated ->
                if (updated) {
                    AccountNotificationMutationOutcome.Success
                } else {
                    AccountNotificationMutationOutcome.Unchanged
                }
            },
            onFailure = { AccountNotificationMutationOutcome.Failure(error) },
        )
}

/** Outcome of a notification mutation, applied optimistically on the client before this resolves. */
internal sealed interface AccountNotificationMutationOutcome {
    data object Success : AccountNotificationMutationOutcome
    data object Unchanged : AccountNotificationMutationOutcome
    data class Failure(val error: AccountUiError) : AccountNotificationMutationOutcome
}
