package su.afk.yummy.tv.feature.details.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import su.afk.yummy.tv.core.designsystem.theme.YummySemanticColors
import su.afk.yummy.tv.domain.account.model.UserAnimeList

@Composable
fun UserAnimeList.statusColor(): Color = when (this) {
    UserAnimeList.WATCHING -> YummySemanticColors.StatusWatching
    UserAnimeList.PLANNED -> YummySemanticColors.StatusPlanned
    UserAnimeList.COMPLETED -> YummySemanticColors.StatusCompleted
    UserAnimeList.POSTPONED -> YummySemanticColors.StatusPostponed
    UserAnimeList.DROPPED -> YummySemanticColors.StatusDropped
}
