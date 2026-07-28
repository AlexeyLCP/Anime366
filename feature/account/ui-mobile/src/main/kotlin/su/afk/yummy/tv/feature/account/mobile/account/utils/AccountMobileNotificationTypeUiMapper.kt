package su.afk.yummy.tv.feature.account.mobile.account.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import su.afk.yummy.tv.feature.account.mobile.R

@Composable
internal fun notificationTypeLabel(type: String): String = when (type) {
    "news" -> stringResource(R.string.account_notification_type_news)
    "edit" -> stringResource(R.string.account_notification_type_edit)
    "message" -> stringResource(R.string.account_notification_type_message)
    "comment" -> stringResource(R.string.account_notification_type_comment)
    "animeupdate" -> stringResource(R.string.account_notification_type_animeupdate)
    "review" -> stringResource(R.string.account_notification_type_review)
    "viewingorderupdate", "viewing_order_update" -> stringResource(R.string.account_notification_type_viewing_order_update)
    "anime_episode" -> stringResource(R.string.account_notification_type_anime_episode)
    "friend" -> stringResource(R.string.account_notification_type_friend)
    "collection" -> stringResource(R.string.account_notification_type_collection)
    "post" -> stringResource(R.string.account_notification_type_post)
    "blogvideo" -> stringResource(R.string.account_notification_type_blogvideo)
    else -> type
}
