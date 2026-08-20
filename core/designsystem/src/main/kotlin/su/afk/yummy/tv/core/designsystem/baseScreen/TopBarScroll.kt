package su.afk.yummy.tv.core.designsystem.baseScreen

import androidx.compose.material3.ExperimentalMaterial3Api

@OptIn(ExperimentalMaterial3Api::class)
sealed interface TopBarScroll {
    data object None : TopBarScroll
    data object Pinned : TopBarScroll
    data object EnterAlways : TopBarScroll
    data object ExitUntilCollapsed : TopBarScroll
}