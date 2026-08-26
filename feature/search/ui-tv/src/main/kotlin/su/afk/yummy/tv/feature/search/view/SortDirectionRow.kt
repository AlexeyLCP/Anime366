package su.afk.yummy.tv.feature.search.view

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp

private const val SORT_DIRECTION_ANIMATION_MILLIS = 180

/**
 * Row that toggles sort direction and shows it with a rotating arrow.
 *
 * The direction is always set, so the row is permanently accented — but without the
 * selection checkmark, since the arrow already carries the state.
 */
@Composable
internal fun SortDirectionRow(
    label: String,
    forward: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val rotation by animateFloatAsState(
        targetValue = if (forward) 0f else 180f,
        animationSpec = tween(durationMillis = SORT_DIRECTION_ANIMATION_MILLIS),
        label = "sortDirectionRotation",
    )
    SelectableRow(
        label = label,
        selected = true,
        onClick = onClick,
        modifier = modifier,
        showSelectedMark = false,
        trailingIcon = {
            Icon(
                imageVector = Icons.Filled.ArrowUpward,
                contentDescription = null,
                modifier = Modifier
                    .padding(start = 6.dp)
                    .size(18.dp)
                    .rotate(rotation),
            )
        },
    )
}
