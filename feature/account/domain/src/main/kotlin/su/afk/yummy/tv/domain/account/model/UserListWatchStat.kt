package su.afk.yummy.tv.domain.account.model

import androidx.compose.runtime.Immutable

@Immutable
data class UserListWatchStat(
    val id: Int,
    val title: String,
    val href: String,
    val seconds: Long,
)
