package su.afk.yummy.tv.domain.account.model

import androidx.compose.runtime.Immutable

@Immutable
data class UserSocialCounts(
    val friends: Int = 0,
    val reviews: Int = 0,
    val comments: Int = 0,
    val posts: Int = 0,
    val collections: Int = 0,
)
