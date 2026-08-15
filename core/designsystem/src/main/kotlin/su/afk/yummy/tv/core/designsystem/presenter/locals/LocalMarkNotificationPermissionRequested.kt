package su.afk.yummy.tv.core.designsystem.presenter.locals

import androidx.compose.runtime.staticCompositionLocalOf

val LocalMarkNotificationPermissionRequested = staticCompositionLocalOf<suspend () -> Unit> { {} }
