package su.afk.yummy.tv.feature.player.model

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import su.afk.yummy.tv.feature.player.common.model.PlayerEndPromptState
import su.afk.yummy.tv.feature.player.common.utils.isVisible

/**
 * Промпты конца эпизода: следующий эпизод (сбрасывается по episodeKey/streamUrl)
 * и финальное действие тайтла (живёт без ключей — как раньше).
 */
@Stable
internal class TvPlayerPromptsState(
    nextEpisodePromptState: MutableState<PlayerEndPromptState>,
    nextEpisodePromptDismissedState: MutableState<Boolean>,
    finalEpisodeActionPromptState: MutableState<PlayerFinalEpisodeAction?>,
) {
    var nextEpisodePrompt: PlayerEndPromptState by nextEpisodePromptState

    /** Пользователь отказался от перехода: не показываем промпт повторно по детекту конца. */
    var nextEpisodePromptDismissed: Boolean by nextEpisodePromptDismissedState
    var finalEpisodeActionPrompt: PlayerFinalEpisodeAction? by finalEpisodeActionPromptState

    val anyVisible: Boolean
        get() = nextEpisodePrompt.isVisible || finalEpisodeActionPrompt != null
}

@Composable
internal fun rememberTvPlayerPromptsState(
    episodeKey: String,
    streamUrl: String,
): TvPlayerPromptsState {
    val nextEpisodePrompt = remember(episodeKey, streamUrl) {
        mutableStateOf<PlayerEndPromptState>(PlayerEndPromptState.Hidden)
    }
    val nextEpisodePromptDismissed = remember(episodeKey, streamUrl) { mutableStateOf(false) }
    val finalEpisodeActionPrompt = remember { mutableStateOf<PlayerFinalEpisodeAction?>(null) }
    return remember(nextEpisodePrompt) {
        TvPlayerPromptsState(
            nextEpisodePromptState = nextEpisodePrompt,
            nextEpisodePromptDismissedState = nextEpisodePromptDismissed,
            finalEpisodeActionPromptState = finalEpisodeActionPrompt,
        )
    }
}
