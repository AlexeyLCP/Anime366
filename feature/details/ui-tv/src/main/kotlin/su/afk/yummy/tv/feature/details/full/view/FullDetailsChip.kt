package su.afk.yummy.tv.feature.details.full.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import su.afk.yummy.tv.core.designsystem.presenter.focus.tvFocusableClick

@Composable
internal fun FullDetailsChip(label: String, onClick: (() -> Unit)? = null) {
    val shape = RoundedCornerShape(999.dp)
    Text(
        text = label,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.86f),
        modifier = Modifier
            .background(
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f),
                shape = shape,
            )
            .then(
                if (onClick != null) {
                    Modifier.tvFocusableClick(onClick = onClick, shape = shape)
                } else {
                    Modifier
                },
            )
            .padding(horizontal = 14.dp, vertical = 7.dp),
    )
}
