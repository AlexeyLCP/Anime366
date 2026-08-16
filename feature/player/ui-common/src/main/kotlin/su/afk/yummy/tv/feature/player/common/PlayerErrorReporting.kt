package su.afk.yummy.tv.feature.player.common

import androidx.media3.common.PlaybackException
import su.afk.yummy.tv.feature.player.PlayerState
import su.afk.yummy.tv.feature.player.common.utils.analyticsType

fun PlaybackException.toPlaybackErrorEvent(positionMs: Long): PlayerState.Event.PlaybackError =
    PlayerState.Event.PlaybackError(
        message = localizedMessage
            ?: message
            ?: errorCodeName,
        errorCode = errorCodeName.takeIf { it.isNotBlank() },
        errorType = analyticsType(),
        positionMs = positionMs,
    )
