package su.afk.yummy.tv.domain.player.usecase

import su.afk.yummy.tv.domain.player.repository.WatchProgressRepository
import javax.inject.Inject

/** Удаляет локальный прогресс серии — снимает ручную отметку о просмотре. */
class ClearEpisodeWatchProgressUseCase @Inject constructor(
    private val repository: WatchProgressRepository,
) {
    suspend operator fun invoke(animeId: Int, episode: String) {
        if (animeId <= 0 || episode.isBlank()) return
        repository.delete(animeId, episode)
    }
}
