package su.afk.yummy.tv.feature.player.view.player

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import su.afk.yummy.tv.core.designsystem.theme.YummySemanticColors
import su.afk.yummy.tv.feature.player.common.PlayerVolumeController
import su.afk.yummy.tv.feature.player.presentation.R

/**
 * Мини-окно «продвинутой» громкости плеера для ТВ: фокусируемая карточка, где
 * ← / → меняют внутреннюю громкость на ±1% (0–200%), а ↓ закрывает панель.
 */
@Composable
internal fun TvPlayerVolumePanel(
    visible: Boolean,
    percent: Int,
    focusRequester: FocusRequester,
    onPercentChange: (Int) -> Unit,
    onExitDown: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        enter = fadeIn(),
        exit = fadeOut(),
    ) {
        val shape = RoundedCornerShape(12.dp)
        val interactionSource = remember { MutableInteractionSource() }
        val focused by interactionSource.collectIsFocusedAsState()
        val colors = MaterialTheme.colorScheme
        val border by animateColorAsState(
            targetValue = if (focused) colors.primary else Color.White.copy(alpha = 0.12f),
            animationSpec = tween(TV_PLAYER_FOCUS_ANIMATION_DURATION_MS),
            label = "tvVolumePanelBorder",
        )
        val fraction = (percent.toFloat() / PlayerVolumeController.MAX_PERCENT).coerceIn(0f, 1f)

        Column(
            modifier = Modifier
                .width(336.dp)
                .clip(shape)
                .background(YummySemanticColors.PanelScrim)
                .border(2.dp, border, shape)
                .focusRequester(focusRequester)
                .focusProperties { canFocus = true }
                .onPreviewKeyEvent { event ->
                    if (event.type != KeyEventType.KeyDown) return@onPreviewKeyEvent false
                    when (event.key) {
                        Key.DirectionRight -> {
                            onPercentChange((percent + 1).coerceAtMost(PlayerVolumeController.MAX_PERCENT))
                            true
                        }

                        Key.DirectionLeft -> {
                            onPercentChange((percent - 1).coerceAtLeast(0))
                            true
                        }

                        Key.DirectionDown -> {
                            onExitDown()
                            true
                        }

                        else -> false
                    }
                }
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = {},
                )
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.player_volume_title),
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White.copy(alpha = 0.62f),
                )
                Text(
                    text = "$percent%",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White,
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(999.dp))
                    .background(Color.White.copy(alpha = 0.20f)),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(fraction)
                        .height(6.dp)
                        .clip(RoundedCornerShape(999.dp))
                        .background(colors.primary),
                )
            }
            Text(
                text = stringResource(R.string.player_volume_hint),
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.5f),
            )
        }
    }
}
