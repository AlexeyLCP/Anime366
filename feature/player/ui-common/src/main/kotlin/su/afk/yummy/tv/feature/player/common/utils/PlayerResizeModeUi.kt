package su.afk.yummy.tv.feature.player.common.utils

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.ScaleFactor
import su.afk.yummy.tv.core.preferences.settings.model.PlayerResizeMode
import su.afk.yummy.tv.core.preferences.settings.model.PlayerZoomLevel

fun playerContentScale(
    resizeMode: PlayerResizeMode,
    zoomLevel: PlayerZoomLevel,
): ContentScale = when (resizeMode) {
    PlayerResizeMode.STRETCH -> ContentScale.FillBounds
    PlayerResizeMode.CROP -> ContentScale.Crop
    PlayerResizeMode.FIT, PlayerResizeMode.ZOOM -> {
        val zoom = if (resizeMode == PlayerResizeMode.ZOOM) {
            1f + zoomLevel.percent / 100f
        } else {
            1f
        }
        object : ContentScale {
            override fun computeScaleFactor(srcSize: Size, dstSize: Size): ScaleFactor {
                val fit = ContentScale.Fit.computeScaleFactor(srcSize, dstSize)
                return ScaleFactor(fit.scaleX * zoom, fit.scaleY * zoom)
            }
        }
    }
}
