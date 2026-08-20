package su.afk.yummy.tv.core.designsystem.locals

import androidx.compose.runtime.staticCompositionLocalOf
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

val LocalNotificationPermissionRequested = staticCompositionLocalOf<Flow<Boolean>> { flowOf(false) }
