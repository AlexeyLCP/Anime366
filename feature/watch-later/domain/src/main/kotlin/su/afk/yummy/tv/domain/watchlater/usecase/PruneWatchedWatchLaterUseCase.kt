package su.afk.yummy.tv.domain.watchlater.usecase

import su.afk.yummy.tv.domain.watchlater.repository.WatchLaterRepository
import javax.inject.Inject

/**
 * Выкидывает из отложенных уже досмотренные серии. Из списка они пропадают и без этого
 * (хранилище фильтрует на чтении), вызов нужен, чтобы таблица не росла бесконечно.
 */
class PruneWatchedWatchLaterUseCase @Inject constructor(
    private val repository: WatchLaterRepository,
) {
    suspend operator fun invoke() = repository.pruneWatched()
}
