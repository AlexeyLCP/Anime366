package su.afk.yummy.tv.core.designsystem.baseScreen

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.statusBars
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

private const val MAX_HEIGHT_FRACTION = 0.85f

/**
 * Высота, дальше которой контент bottom sheet не должен растягиваться (85% высоты,
 * доступной под статус-баром). Считать долю от полной [LocalConfiguration.screenHeightDp]
 * без вычета статус-бара нельзя: на широких/невысоких окнах (например, разложенный
 * foldable) 85% полного экрана может превышать высоту под статус-баром, и шторка
 * заезжает под него.
 */
@Composable
fun rememberBottomSheetMaxHeight(): Dp {
    val statusBarHeight = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val availableHeight = LocalConfiguration.current.screenHeightDp.dp - statusBarHeight
    return availableHeight * MAX_HEIGHT_FRACTION
}
