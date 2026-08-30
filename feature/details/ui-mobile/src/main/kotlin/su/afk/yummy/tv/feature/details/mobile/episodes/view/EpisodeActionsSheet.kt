package su.afk.yummy.tv.feature.details.mobile.episodes.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import su.afk.yummy.tv.core.designsystem.baseScreen.BaseBottomSheet
import su.afk.yummy.tv.feature.details.episodes.EpisodesState
import su.afk.yummy.tv.feature.details.mobile.R

/** Действия над серией по долгому нажатию: пока это только отметка о просмотре. */
@Composable
@OptIn(ExperimentalMaterial3Api::class)
internal fun EpisodeActionsSheet(
    action: EpisodesState.EpisodeAction,
    onToggleWatched: () -> Unit,
    onDismiss: () -> Unit,
) {
    BaseBottomSheet(
        onDismissRequest = onDismiss,
        title = stringResource(R.string.details_mobile_episode_actions_title, action.episode),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 18.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        EpisodeSheetActionButton(
            text = if (action.isWatched) {
                stringResource(R.string.details_mobile_unmark_episode_watched)
            } else {
                stringResource(R.string.details_mobile_mark_episode_watched)
            },
            icon = if (action.isWatched) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
            onClick = onToggleWatched,
        )
    }
}
