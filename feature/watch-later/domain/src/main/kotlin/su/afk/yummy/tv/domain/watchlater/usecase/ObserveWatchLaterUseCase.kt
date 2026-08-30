package su.afk.yummy.tv.domain.watchlater.usecase

import kotlinx.coroutines.flow.Flow
import su.afk.yummy.tv.domain.watchlater.model.WatchLaterItem
import su.afk.yummy.tv.domain.watchlater.repository.WatchLaterRepository
import javax.inject.Inject

/** Все отложенные серии, свежие сверху. Досмотренные отсеиваются хранилищем. */
class ObserveWatchLaterUseCase @Inject constructor(
    private val repository: WatchLaterRepository,
) {
    operator fun invoke(): Flow<List<WatchLaterItem>> = repository.observeAll()
}
