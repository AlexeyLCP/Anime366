package su.afk.yummy.tv.feature.account.account.model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

internal data class ProfileCounterItem(
    val label: String,
    val count: Int,
    val color: Color,
    val icon: ImageVector,
)
