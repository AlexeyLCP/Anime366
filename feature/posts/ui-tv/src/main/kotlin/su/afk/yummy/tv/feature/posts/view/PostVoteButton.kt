package su.afk.yummy.tv.feature.posts.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import su.afk.yummy.tv.core.designsystem.focus.tvFocusableClick
import su.afk.yummy.tv.core.designsystem.theme.YummySemanticColors

/** Кнопка голоса (лайк/дизлайк) с семантическим цветом и видимым ТВ-фокусом. */
@Composable
internal fun PostVoteButton(
    isLike: Boolean,
    count: Int,
    selected: Boolean,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val shape = RoundedCornerShape(8.dp)
    val voteColor = if (isLike) YummySemanticColors.Like else YummySemanticColors.Dislike
    val tint = if (selected) voteColor else voteColor.copy(alpha = 0.72f)
    val bgColor =
        if (selected) voteColor.copy(alpha = 0.20f) else MaterialTheme.colorScheme.surfaceVariant
    Box(
        modifier = modifier
            .width(112.dp)
            .alpha(if (enabled) 1f else 0.5f)
            .tvFocusableClick(onClick = { if (enabled) onClick() }, shape = shape)
            .background(bgColor, shape)
            .padding(horizontal = 16.dp, vertical = 10.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(
                imageVector = if (isLike) Icons.Default.ThumbUp else Icons.Default.ThumbDown,
                contentDescription = null,
                tint = tint,
                modifier = Modifier.size(18.dp),
            )
            Text(
                text = count.toString(),
                color = tint,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
            )
        }
    }
}
