package su.afk.yummy.tv.core.model.settings

data class PlayerResizeSettings(
    val resizeMode: PlayerResizeMode = PlayerResizeMode.FIT,
    val zoomLevel: PlayerZoomLevel = PlayerZoomLevel.PERCENT_10,
)
