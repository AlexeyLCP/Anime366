package su.afk.yummy.tv.core.designsystem.presenter.baseScreen

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

private const val MAX_HEIGHT_FRACTION = 0.85f

/** Высота, дальше которой контент bottom sheet не должен растягиваться (95% экрана). */
@Composable
fun rememberBottomSheetMaxHeight(): Dp =
    (LocalConfiguration.current.screenHeightDp * MAX_HEIGHT_FRACTION).dp
