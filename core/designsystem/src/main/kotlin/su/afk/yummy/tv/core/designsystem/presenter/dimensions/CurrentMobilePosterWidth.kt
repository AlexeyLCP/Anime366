package su.afk.yummy.tv.core.designsystem.presenter.dimensions

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import su.afk.yummy.tv.core.designsystem.presenter.locals.LocalPosterCardSize
import su.afk.yummy.tv.core.model.settings.PosterCardSize

@Composable
fun currentMobilePosterWidth(): Dp = LocalPosterCardSize.current.mobilePosterWidth

private val PosterCardSize.mobilePosterWidth: Dp
    get() = when (this) {
        PosterCardSize.COMPACT -> 104.dp
        PosterCardSize.STANDARD -> 140.dp
        PosterCardSize.LARGE -> 168.dp
    }
