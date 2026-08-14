package su.afk.yummy.tv.core.preferences.settings.model

import su.afk.yummy.tv.core.model.settings.AppTheme
import su.afk.yummy.tv.core.model.settings.BackgroundStyle
import su.afk.yummy.tv.core.model.settings.PosterCardSize
import su.afk.yummy.tv.core.model.settings.PosterQuality

data class MainSettingsSnapshot(
    val appTheme: AppTheme,
    val backgroundStyle: BackgroundStyle,
    val posterQuality: PosterQuality,
    val posterCardSize: PosterCardSize,
    val yaniNickname: String,
    val yaniAvatarUrl: String,
    val yaniUnreadNotificationsCount: Int,
)
