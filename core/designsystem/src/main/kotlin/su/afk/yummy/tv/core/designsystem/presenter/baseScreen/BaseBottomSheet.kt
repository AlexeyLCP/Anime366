package su.afk.yummy.tv.core.designsystem.presenter.baseScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp

private const val MAX_HEIGHT_FRACTION = 0.85f

/** Высота, дальше которой контент bottom sheet не должен растягиваться (95% экрана). */
@Composable
fun rememberBottomSheetMaxHeight(): Dp =
    (LocalConfiguration.current.screenHeightDp * MAX_HEIGHT_FRACTION).dp

/**
 * Гасит остаточный scroll/fling, когда список внутри шторки короче её максимальной высоты и
 * упирается в свой нижний край: без этого остаток жеста уходит через nested scroll в сам
 * `ModalBottomSheet`, и тот дёргается вверх и потом долго анимированно возвращается обратно.
 * Жест "потянуть вниз от начала списка" (закрытие свайпом) не трогаем — пропускаем как есть.
 */
@Composable
private fun rememberBottomOverscrollGuard(): NestedScrollConnection = remember {
    object : NestedScrollConnection {
        override fun onPostScroll(
            consumed: Offset,
            available: Offset,
            source: NestedScrollSource,
        ): Offset = if (available.y < 0f) available else Offset.Zero

        override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity =
            if (available.y < 0f) available else Velocity.Zero
    }
}

/** Общая обёртка над [ModalBottomSheet]: заголовок, стандартные отступы и высота не более 95% экрана. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BaseBottomSheet(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    title: String? = null,
    titleContent: (@Composable () -> Unit)? = null,
    contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp),
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    content: @Composable ColumnScope.() -> Unit,
) {
    val maxHeight = rememberBottomSheetMaxHeight()

    ModalBottomSheet(
        modifier = modifier,
        sheetState = sheetState,
        onDismissRequest = onDismissRequest,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = maxHeight)
                .navigationBarsPadding()
                .padding(contentPadding)
                .nestedScroll(rememberBottomOverscrollGuard()),
            verticalArrangement = verticalArrangement,
        ) {
            when {
                titleContent != null -> titleContent()
                title != null -> Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 14.dp),
                )
            }
            content()
        }
    }
}

/**
 * Вариант [BaseBottomSheet] для контента, который сам управляет своим корневым layout'ом
 * (например, [androidx.compose.foundation.lazy.LazyColumn] с собственными insets/contentPadding).
 * [content] получает [maxHeight] сам ограничивает себя через `Modifier.heightIn(max = maxHeight)`.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BaseBottomSheetCustom(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    content: @Composable (maxHeight: Dp) -> Unit,
) {
    val maxHeight = rememberBottomSheetMaxHeight()

    ModalBottomSheet(
        modifier = modifier,
        sheetState = sheetState,
        onDismissRequest = onDismissRequest,
    ) {
        Box(modifier = Modifier.nestedScroll(rememberBottomOverscrollGuard())) {
            content(maxHeight)
        }
    }
}
