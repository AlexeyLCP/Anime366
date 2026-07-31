package su.afk.yummy.tv.feature.details.mobile.rating.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import su.afk.yummy.tv.domain.account.model.UserAnimeList

@Composable
internal fun UserAnimeList.statusColor(): Color = when (this) {
    UserAnimeList.WATCHING -> Color(0xFFFF6B6B)
    UserAnimeList.PLANNED -> Color(0xFFA678E8)
    UserAnimeList.COMPLETED -> Color(0xFF69D38B)
    UserAnimeList.POSTPONED -> Color(0xFFFFC857)
    UserAnimeList.DROPPED -> Color(0xFF9CA3AF)
}
