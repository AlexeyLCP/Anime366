package su.afk.yummy.tv.feature.details.mobile.episodes.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import su.afk.yummy.tv.core.designsystem.baseScreen.BaseBottomSheet
import su.afk.yummy.tv.feature.details.episodes.EpisodesState
import su.afk.yummy.tv.feature.details.mobile.R

@Composable
@OptIn(ExperimentalMaterial3Api::class)
internal fun EpisodeDownloadQualitySheet(
    selection: EpisodesState.EpisodeDownloadQualitySelection,
    onSelected: (EpisodesState.EpisodeDownloadQualityOption) -> Unit,
    onDismiss: () -> Unit,
) {
    BaseBottomSheet(
        onDismissRequest = onDismiss,
        title = stringResource(R.string.details_mobile_download_quality_title),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 18.dp),
    ) {
        Text(
            text = stringResource(R.string.details_mobile_download_quality_prompt),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 4.dp),
        )
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            items(selection.options, key = { "${it.label}|${it.url}" }) { option ->
                TextButton(
                    onClick = { onSelected(option) },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(text = option.label, modifier = Modifier.fillMaxWidth())
                }
            }
        }
    }
}
