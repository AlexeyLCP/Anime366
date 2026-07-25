package su.afk.yummy.tv.feature.player.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface PlayerVolumeControllerEntryPoint {
    fun playerVolumeController(): PlayerVolumeController
}

/** Тот же процесс-синглтон [PlayerVolumeController], что читает аудио-процессор в сервисе. */
@Composable
fun rememberPlayerVolumeController(): PlayerVolumeController {
    val context = LocalContext.current.applicationContext
    return remember(context) {
        EntryPointAccessors.fromApplication(context, PlayerVolumeControllerEntryPoint::class.java)
            .playerVolumeController()
    }
}
