package su.afk.yummy.tv.core.designsystem.locals

import androidx.compose.runtime.staticCompositionLocalOf

val LocalMarkNotificationPermissionRequested = staticCompositionLocalOf<suspend () -> Unit> { {} }
