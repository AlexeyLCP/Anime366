package su.afk.yummy.tv.domain.watchlater.usecase

import kotlinx.coroutines.flow.Flow
import su.afk.yummy.tv.domain.watchlater.repository.WatchLaterRepository
import javax.inject.Inject

/**
 * Нормализованные номера отложенных серий одного тайтла — нужны экрану серий, чтобы понимать
 * направление действия в меню долгого нажатия.
 */
class ObserveWatchLaterEpisodesUseCase @Inject constructor(
    private val repository: WatchLaterRepository,
) {
    operator fun invoke(animeId: Int): Flow<Set<String>> = repository.observeEpisodes(animeId)
}
