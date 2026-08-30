package su.afk.yummy.tv.feature.account.mobile.account.model

import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.collections.immutable.PersistentList

internal data class AccountMobileProfileStatsPageModel(
    val title: String,
    val icon: ImageVector,
    val slices: PersistentList<AccountMobileProfileStatSlice>,
    val valueType: AccountMobileProfileStatsValueType,
    val legendColumns: Int = 2,
)
