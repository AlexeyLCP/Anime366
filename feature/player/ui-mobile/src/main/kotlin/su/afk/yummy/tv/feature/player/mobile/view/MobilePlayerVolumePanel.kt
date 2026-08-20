package su.afk.yummy.tv.feature.player.mobile.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeDown
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import su.afk.yummy.tv.core.designsystem.theme.YummySemanticColors
import su.afk.yummy.tv.feature.player.common.PlayerVolumeController
import kotlin.math.roundToInt

/**
 * Компактное мини-окно «продвинутой» громкости плеера: горизонтальный ползунок 0–200%
 * с шагом 1%, независимый от системной громкости. Показывается над панелью управления.
 */
@Composable
internal fun MobilePlayerVolumePanel(
    percent: Int,
    onPercentChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val icon = when {
        percent <= 0 -> Icons.AutoMirrored.Filled.VolumeOff
        percent < 100 -> Icons.AutoMirrored.Filled.VolumeDown
        else -> Icons.AutoMirrored.Filled.VolumeUp
    }
    Row(
        modifier = modifier
            .widthIn(max = 360.dp)
            .background(YummySemanticColors.PanelScrim, RoundedCornerShape(14.dp))
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = Color.White)
        Slider(
            value = percent.toFloat(),
            onValueChange = { onPercentChange(it.roundToInt()) },
            valueRange = 0f..PlayerVolumeController.MAX_PERCENT.toFloat(),
            // 199 промежуточных делений → 201 позиция (0..200), шаг ровно 1%.
            steps = PlayerVolumeController.MAX_PERCENT - 1,
            colors = SliderDefaults.colors(
                thumbColor = Color.White,
                activeTrackColor = Color.White,
                inactiveTrackColor = Color.White.copy(alpha = 0.3f),
                activeTickColor = Color.Transparent,
                inactiveTickColor = Color.Transparent,
            ),
            modifier = Modifier.weight(1f),
        )
        Text(
            text = "$percent%",
            color = Color.White,
            fontSize = 14.sp,
            textAlign = TextAlign.End,
            modifier = Modifier.widthIn(min = 44.dp),
        )
    }
}
