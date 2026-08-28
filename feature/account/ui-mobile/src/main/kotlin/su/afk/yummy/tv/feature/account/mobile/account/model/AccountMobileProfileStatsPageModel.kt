package su.afk.yummy.tv.feature.account.mobile.account.model

import kotlinx.collections.immutable.PersistentList

internal data class AccountMobileProfileStatsPageModel(
    val title: String,
    val slices: PersistentList<AccountMobileProfileStatSlice>,
    val valueType: AccountMobileProfileStatsValueType,
    val legendColumns: Int = 2,
)
