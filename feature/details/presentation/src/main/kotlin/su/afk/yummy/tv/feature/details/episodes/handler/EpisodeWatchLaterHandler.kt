package su.afk.yummy.tv.feature.details.episodes.handler

import su.afk.yummy.tv.domain.watchlater.usecase.AddWatchLaterEpisodeUseCase
import su.afk.yummy.tv.domain.watchlater.usecase.RemoveWatchLaterEpisodeUseCase
import javax.inject.Inject

/**
 * Пометка «отложить просмотр» и её снятие. Список чисто локальный — серверного аналога нет,
 * поэтому ходить в сеть и перечитывать серии здесь не нужно.
 */
internal class EpisodeWatchLaterHandler @Inject constructor(
    private val addWatchLaterEpisode: AddWatchLaterEpisodeUseCase,
    private val removeWatchLaterEpisode: RemoveWatchLaterEpisodeUseCase,
) {

    suspend fun toggle(
        animeId: Int,
        episode: String,
        isInWatchLater: Boolean,
        meta: EpisodeWatchedHandler.EpisodeMeta,
    ) {
        if (isInWatchLater) {
            removeWatchLaterEpisode(animeId, episode)
        } else {
            addWatchLaterEpisode(
                animeId = animeId,
                episode = episode,
                animeTitle = meta.animeTitle,
                posterUrl = meta.posterUrl,
                screenshotUrl = meta.screenshotUrl,
            )
        }
    }
}
