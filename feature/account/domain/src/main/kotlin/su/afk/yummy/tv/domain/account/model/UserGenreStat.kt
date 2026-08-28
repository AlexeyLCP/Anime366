package su.afk.yummy.tv.domain.account.model

import androidx.compose.runtime.Immutable

@Immutable
data class UserGenreStat(
    val id: Int,
    val title: String,
    val count: Int,
)
