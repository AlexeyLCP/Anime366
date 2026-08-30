package su.afk.yummy.tv.feature.details.mobile.details.view

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import su.afk.yummy.tv.feature.details.details.model.DubbingOption
import su.afk.yummy.tv.feature.details.details.model.DubbingPickerState
import su.afk.yummy.tv.feature.details.mobile.R
import su.afk.yummy.tv.feature.details.mobile.details.model.MobilePickerItem
import su.afk.yummy.tv.feature.details.utils.dubbingKind
import su.afk.yummy.tv.feature.details.utils.dubbingTeam

@Composable
internal fun DubbingDialog(
    picker: DubbingPickerState,
    onSelected: (DubbingOption, Boolean) -> Unit,
    onDismiss: () -> Unit,
) {
    var rememberChoice by remember { mutableStateOf(true) }
    val items = remember(picker.options, rememberChoice) {
        val grouped = picker.options.groupBy { it.item.name.dubbingKind() }
        grouped.flatMap { (kind, options) ->
            buildList {
                if (grouped.size > 1) {
                    add(
                        MobilePickerItem(
                            key = "kind:$kind",
                            title = kind,
                            enabled = false,
                            onClick = {},
                        ),
                    )
                }
                options.forEach { option ->
                    add(
                        MobilePickerItem(
                            key = option.item.name,
                            title = option.item.name.dubbingTeam(),
                            subtitle = option.item.supportedBalancers,
                            views = option.item.views,
                            onClick = { onSelected(option, rememberChoice) },
                        ),
                    )
                }
            }
        }
    }
    MobilePickerBottomSheet(
        title = stringResource(R.string.details_mobile_episode_dubbings_title, picker.episode),
        onDismiss = onDismiss,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Checkbox(
                checked = rememberChoice,
                onCheckedChange = { rememberChoice = it },
            )
            Text(
                text = stringResource(R.string.details_mobile_remember_dubbing),
                style = MaterialTheme.typography.bodyMedium,
            )
        }
        MobilePickerItems(items = items)
    }
}
