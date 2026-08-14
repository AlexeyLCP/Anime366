package su.afk.yummy.tv.core.preferences.settings

import kotlinx.coroutines.flow.Flow
import su.afk.yummy.tv.core.preferences.settings.model.PlayerMobileVideoTransformSettings
import su.afk.yummy.tv.core.preferences.settings.model.PlayerOrientationMode
import su.afk.yummy.tv.core.preferences.settings.model.PlayerResizeMode
import su.afk.yummy.tv.core.preferences.settings.model.PlayerResizeSettings
import su.afk.yummy.tv.core.preferences.settings.model.PlayerZoomLevel
import su.afk.yummy.tv.core.preferences.settings.model.PreferredPlayer
import su.afk.yummy.tv.core.preferences.settings.model.PreferredVideoQuality

/** Поведение и настройки плеера: воспроизведение, жесты, громкость, размер кадра. */
interface PlayerSettingsStore {

    val preferredPlayer: Flow<PreferredPlayer>
    val preferredVideoQuality: Flow<PreferredVideoQuality>
    val autoSkipOpeningsEndings: Flow<Boolean>

    /** Показывать участок опенинга на полосе прогресса плеера. */
    val showOpeningOnTimeline: Flow<Boolean>
    val autoPlayNextEpisode: Flow<Boolean>

    /** Спрашивать озвучку при нажатии "Смотреть", вместо автовыбора самой популярной. */
    val askDubbingOnWatch: Flow<Boolean>
    val pictureInPictureEnabled: Flow<Boolean>

    /** Принудительная альбомная ориентация плеера, не зависящая от системной блокировки поворота. */
    val playerOrientationMode: Flow<PlayerOrientationMode>
    val suggestNextEpisodeOnWatched: Flow<Boolean>
    val refreshContinueWatchingProgressOnLaunch: Flow<Boolean>
    val mobilePlayerGestureTutorialDismissed: Flow<Boolean>
    val tvPlayerControlsTutorialDismissed: Flow<Boolean>
    val tvPlayerVolumeKeysEnabled: Flow<Boolean>
    val advancedPlayerVolumeEnabled: Flow<Boolean>
    val advancedPlayerVolumePercent: Flow<Int>

    /** Стабилизация громкости (сжатие динамического диапазона аудио). */
    val volumeStabilizationEnabled: Flow<Boolean>
    val playerResizeMode: Flow<PlayerResizeMode>
    val playerZoomLevel: Flow<PlayerZoomLevel>

    fun playerResizeSettings(
        animeId: Int,
        animeTitle: String,
        playerName: String,
    ): Flow<PlayerResizeSettings>

    fun playerMobileVideoTransformSettings(
        animeId: Int,
        animeTitle: String,
        playerName: String,
    ): Flow<PlayerMobileVideoTransformSettings>

    suspend fun setPreferredPlayer(player: PreferredPlayer)
    suspend fun setPreferredVideoQuality(quality: PreferredVideoQuality)
    suspend fun setAutoSkipOpeningsEndings(enabled: Boolean)
    suspend fun setShowOpeningOnTimeline(enabled: Boolean)
    suspend fun setAutoPlayNextEpisode(enabled: Boolean)
    suspend fun setAskDubbingOnWatch(enabled: Boolean)
    suspend fun setPictureInPictureEnabled(enabled: Boolean)
    suspend fun setPlayerOrientationMode(mode: PlayerOrientationMode)
    suspend fun setSuggestNextEpisodeOnWatched(enabled: Boolean)
    suspend fun setRefreshContinueWatchingProgressOnLaunch(enabled: Boolean)
    suspend fun dismissMobilePlayerGestureTutorial()
    suspend fun resetMobilePlayerGestureTutorial()
    suspend fun dismissTvPlayerControlsTutorial()
    suspend fun resetTvPlayerControlsTutorial()
    suspend fun setTvPlayerVolumeKeysEnabled(enabled: Boolean)
    suspend fun setAdvancedPlayerVolumeEnabled(enabled: Boolean)
    suspend fun setAdvancedPlayerVolumePercent(percent: Int)
    suspend fun setVolumeStabilizationEnabled(enabled: Boolean)
    suspend fun setPlayerResizeMode(mode: PlayerResizeMode)
    suspend fun setPlayerZoomLevel(level: PlayerZoomLevel)

    suspend fun setPlayerResizeSettings(
        animeId: Int,
        animeTitle: String,
        playerName: String,
        settings: PlayerResizeSettings,
    )

    suspend fun setPlayerMobileVideoTransformSettings(
        animeId: Int,
        animeTitle: String,
        playerName: String,
        settings: PlayerMobileVideoTransformSettings,
    )
}
