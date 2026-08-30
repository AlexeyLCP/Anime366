package su.afk.yummy.tv.feature.account.account.model

import androidx.compose.ui.graphics.vector.ImageVector

internal data class ProfileStatsPageModel(
    val title: String,
    val icon: ImageVector,
    val slices: List<ProfileStatSlice>,
    val valueType: ProfileStatsValueType,
    val compactLegend: Boolean = false,
)
