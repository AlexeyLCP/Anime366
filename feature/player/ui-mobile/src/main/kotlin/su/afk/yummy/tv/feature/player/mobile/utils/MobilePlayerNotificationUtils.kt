package su.afk.yummy.tv.feature.player.mobile.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import su.afk.yummy.tv.feature.player.PlayerState
import su.afk.yummy.tv.feature.player.common.buildPlayerPlaybackKey
import su.afk.yummy.tv.feature.player.common.mediaMimeType
import su.afk.yummy.tv.feature.player.common.playerAudioTrackPolicyFor
import su.afk.yummy.tv.feature.player.common.playerSilentReconnectEnabled
import su.afk.yummy.tv.feature.player.common.playerUseRotatingHlsCacheKeys
import su.afk.yummy.tv.feature.player.common.service.PlayerMediaItemConfig
import su.afk.yummy.tv.feature.player.mobile.model.MobilePlayerNotificationMeta
import su.afk.yummy.tv.feature.player.model.PlayerPlaybackUiState
import su.afk.yummy.tv.feature.player.presentation.R
import su.afk.yummy.tv.feature.player.utils.selectedAllohaSubtitle

@Composable
internal fun mobilePlayerNotificationMeta(ui: PlayerPlaybackUiState): MobilePlayerNotificationMeta {
    val subtitle = ui.activeEpisode.takeIf { it.isNotBlank() }?.let {
        stringResource(R.string.player_notification_episode, it)
    }
    val description = when {
        ui.activeBalancerName.isNotBlank() && ui.activeDubbing.isNotBlank() ->
            stringResource(
                R.string.player_notification_details,
                ui.activeDubbing,
                ui.activeBalancerName,
            )

        else -> ui.activeBalancerName.ifBlank { ui.activeDubbing }.takeIf { it.isNotBlank() }
    }
    val contentText = listOfNotNull(
        subtitle,
        ui.activeDubbing.takeIf { it.isNotBlank() },
        ui.activeBalancerName.takeIf { it.isNotBlank() },
    ).joinToString(separator = " • ")
    return MobilePlayerNotificationMeta(
        subtitle = subtitle,
        description = description,
        contentText = contentText,
    )
}

internal fun buildMobilePlayerPlaybackKey(state: PlayerState.State, url: String): String =
    buildPlayerPlaybackKey(
        url = url,
        retryKey = state.retryKey,
        headers = state.streamHeaders,
        // Side-loaded subtitles live on the MediaItem itself, so a different pick must produce a
        // different playback key for the player to re-prepare with it.
        offlineCacheKeySegment = "sub=${state.selectedAllohaSubtitle()?.url.orEmpty()}",
    )

internal fun buildMobileMediaItemKey(
    playbackKey: String,
    animeTitle: String,
    meta: MobilePlayerNotificationMeta,
    artworkUrl: String?,
): String = buildString {
    append(playbackKey)
    append('|').append(animeTitle)
    append('|').append(meta.subtitle.orEmpty())
    append('|').append(meta.description.orEmpty())
    append('|').append(meta.contentText)
    append('|').append(artworkUrl.orEmpty())
}

internal fun buildMobilePlayerMediaItemConfig(
    playbackKey: String,
    mediaItemKey: String,
    url: String,
    episodeUrl: String,
    state: PlayerState.State,
    meta: MobilePlayerNotificationMeta,
    artworkUrl: String?,
): PlayerMediaItemConfig = PlayerMediaItemConfig(
    playbackKey = playbackKey,
    mediaItemKey = mediaItemKey,
    url = url,
    title = state.animeTitle,
    artist = meta.contentText,
    subtitle = meta.subtitle,
    description = meta.description,
    artworkUrl = artworkUrl,
    durationMs = state.playbackDurationMs,
    headers = state.streamHeaders,
    offlineCacheKey = state.offlineCacheKey,
    offlineCacheKeyScheme = state.offlineCacheKeyScheme,
    isOfflinePlayback = state.isOfflinePlayback,
    isLocalFile = state.isLocalFile,
    useRotatingHlsCacheKeys = playerUseRotatingHlsCacheKeys(
        isOfflinePlayback = state.isOfflinePlayback,
        episodeUrl = episodeUrl,
    ),
    audioTrackPolicy = playerAudioTrackPolicyFor(episodeUrl),
    playbackPositionMs = state.playbackPositionMs,
    resumeFromMs = state.resumeFromMs,
    subtitleUrl = state.selectedAllohaSubtitle()?.url,
    subtitleMimeType = state.selectedAllohaSubtitle()?.mediaMimeType(),
    subtitleLanguage = state.selectedAllohaSubtitle()?.language,
    subtitleLabel = state.selectedAllohaSubtitle()?.label,
    silentReconnectEnabled = playerSilentReconnectEnabled(
        episodeUrl = episodeUrl,
        isOfflinePlayback = state.isOfflinePlayback,
        isLocalFile = state.isLocalFile,
    ),
)
