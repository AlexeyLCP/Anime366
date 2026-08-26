package su.afk.yummy.tv.feature.player.common.utils

import su.afk.yummy.tv.feature.player.common.model.PlayerEndPromptState

val PlayerEndPromptState.isVisible: Boolean
    get() = this !is PlayerEndPromptState.Hidden

fun playerEndPromptFor(
    autoPlayNextEpisode: Boolean,
    delaySeconds: Int = PLAYER_END_PROMPT_COUNTDOWN_SECONDS,
): PlayerEndPromptState =
    if (autoPlayNextEpisode) {
        PlayerEndPromptState.WithCountdown(delaySeconds)
    } else {
        PlayerEndPromptState.WithoutCountdown
    }

/** ON_PAUSE: активный отсчёт вырождается в промпт без отсчёта. */
fun PlayerEndPromptState.downgradedCountdown(): PlayerEndPromptState =
    if (this is PlayerEndPromptState.WithCountdown) {
        PlayerEndPromptState.WithoutCountdown
    } else {
        this
    }

/** Часть потоков останавливается чуть раньше duration — считаем это концом эпизода. */
fun isAtPlayerEnd(positionMs: Long, durationMs: Long): Boolean =
    durationMs > 0L && durationMs - positionMs <= PLAYER_END_POSITION_TOLERANCE_MS

const val PLAYER_END_PROMPT_COUNTDOWN_SECONDS = 10

const val PLAYER_END_POSITION_TOLERANCE_MS = 500L
