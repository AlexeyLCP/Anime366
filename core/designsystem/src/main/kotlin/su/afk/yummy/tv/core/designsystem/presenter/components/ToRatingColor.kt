package su.afk.yummy.tv.core.designsystem.presenter.components

import androidx.compose.ui.graphics.Color
import su.afk.yummy.tv.core.designsystem.presenter.theme.YummySemanticColors

fun Double.toRatingColor(): Color = when {
    this < 6.0 -> YummySemanticColors.RatingBadgeLow
    this < 8.0 -> YummySemanticColors.StatusPostponed
    else -> YummySemanticColors.RatingBadgeHigh
}
