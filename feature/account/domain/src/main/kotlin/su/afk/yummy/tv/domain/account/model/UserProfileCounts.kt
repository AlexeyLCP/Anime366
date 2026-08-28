package su.afk.yummy.tv.domain.account.model

import androidx.compose.runtime.Immutable

@Immutable
data class UserProfileCounts(
    val watching: Int = 0,
    val planned: Int = 0,
    val completed: Int = 0,
    val dropped: Int = 0,
    val postponed: Int = 0,
    val favorite: Int = 0,
)
