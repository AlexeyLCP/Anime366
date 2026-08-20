package su.afk.yummy.tv.core.designsystem.dimensions

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import su.afk.yummy.tv.core.designsystem.locals.LocalPosterCardSize
import su.afk.yummy.tv.core.model.settings.PosterCardSize

@Composable
fun currentTvHomeFeedCardDimensions(): PosterCardDimensions =
    LocalPosterCardSize.current.tvHomeFeedCardDimensions

private val PosterCardSize.tvHomeFeedCardDimensions: PosterCardDimensions
    get() = when (this) {
        PosterCardSize.COMPACT -> posterCardDimensions(width = 152.dp)
        PosterCardSize.STANDARD -> posterCardDimensions(width = 180.dp)
        PosterCardSize.LARGE -> posterCardDimensions(width = 208.dp)
    }

private fun posterCardDimensions(width: Dp): PosterCardDimensions = PosterCardDimensions(
    width = width,
    posterHeight = width / TITLE_POSTER_ASPECT_RATIO,
)
