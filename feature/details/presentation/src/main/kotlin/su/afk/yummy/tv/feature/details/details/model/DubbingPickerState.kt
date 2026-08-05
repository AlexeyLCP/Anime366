package su.afk.yummy.tv.feature.details.details.model

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableList

@Immutable
data class DubbingPickerState(
    val episode: String,
    val options: ImmutableList<DubbingOption>,
    val episodeTitle: String? = null,
)
