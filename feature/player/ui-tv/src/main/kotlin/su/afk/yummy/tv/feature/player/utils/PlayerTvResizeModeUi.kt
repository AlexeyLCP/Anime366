package su.afk.yummy.tv.feature.player.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import su.afk.yummy.tv.core.preferences.settings.PlayerResizeMode
import su.afk.yummy.tv.core.preferences.settings.PlayerZoomLevel
import su.afk.yummy.tv.feature.player.common.utils.playerContentScale
import su.afk.yummy.tv.feature.player.presentation.R

internal fun tvPlayerContentScale(
    resizeMode: PlayerResizeMode,
    zoomLevel: PlayerZoomLevel,
) = playerContentScale(resizeMode, zoomLevel)

@Composable
internal fun PlayerResizeMode.tvResizeLabel(): String = when (this) {
    PlayerResizeMode.FIT -> stringResource(R.string.player_resize_fit)
    PlayerResizeMode.ZOOM -> stringResource(R.string.player_resize_zoom)
    PlayerResizeMode.STRETCH -> stringResource(R.string.player_resize_stretch)
    PlayerResizeMode.CROP -> stringResource(R.string.player_resize_crop)
}

@Composable
internal fun PlayerResizeMode.tvResizeMeta(): String = when (this) {
    PlayerResizeMode.FIT -> stringResource(R.string.player_resize_fit_meta)
    PlayerResizeMode.ZOOM -> stringResource(R.string.player_resize_zoom_meta)
    PlayerResizeMode.STRETCH -> stringResource(R.string.player_resize_stretch_meta)
    PlayerResizeMode.CROP -> stringResource(R.string.player_resize_crop_meta)
}

@Composable
internal fun PlayerZoomLevel.tvZoomLevelLabel(): String =
    stringResource(R.string.player_zoom_level_percent, percent)
