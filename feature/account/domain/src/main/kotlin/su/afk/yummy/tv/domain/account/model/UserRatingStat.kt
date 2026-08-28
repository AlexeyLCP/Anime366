package su.afk.yummy.tv.domain.account.model

import androidx.compose.runtime.Immutable

@Immutable
data class UserRatingStat(
    val rating: Int,
    val count: Int,
)
