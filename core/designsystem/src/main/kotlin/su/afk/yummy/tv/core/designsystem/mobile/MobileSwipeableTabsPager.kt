package su.afk.yummy.tv.core.designsystem.mobile

import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Тонкая обёртка над [HorizontalPager], привязанная к [MobileSwipeableTabsState].
 */
@Composable
fun MobileSwipeableTabsPager(
    state: MobileSwipeableTabsState,
    modifier: Modifier = Modifier,
    key: ((page: Int) -> Any)? = null,
    pageContent: @Composable (page: Int) -> Unit,
) {
    HorizontalPager(
        state = state.pagerState,
        key = key,
        modifier = modifier,
    ) { page ->
        pageContent(page)
    }
}
