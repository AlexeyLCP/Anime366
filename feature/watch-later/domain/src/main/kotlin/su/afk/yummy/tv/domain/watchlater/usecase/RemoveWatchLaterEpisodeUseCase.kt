package su.afk.yummy.tv.domain.watchlater.usecase

import su.afk.yummy.tv.domain.watchlater.repository.WatchLaterRepository
import javax.inject.Inject

/** Снимает пометку «отложено» с серии. */
class RemoveWatchLaterEpisodeUseCase @Inject constructor(
    private val repository: WatchLaterRepository,
) {
    suspend operator fun invoke(animeId: Int, episode: String) {
        if (animeId <= 0 || episode.isBlank()) return
        repository.remove(animeId, episode)
    }
}
