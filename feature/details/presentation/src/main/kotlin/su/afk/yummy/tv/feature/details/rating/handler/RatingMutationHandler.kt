package su.afk.yummy.tv.feature.details.rating.handler

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import su.afk.yummy.tv.core.utils.coroutines.runSuspendCatching
import su.afk.yummy.tv.domain.account.model.AnimeListStats
import su.afk.yummy.tv.domain.account.model.AnimeRatingSummary
import su.afk.yummy.tv.domain.account.usecase.DeleteAnimeRatingUseCase
import su.afk.yummy.tv.domain.account.usecase.GetAnimeListStatsUseCase
import su.afk.yummy.tv.domain.account.usecase.GetAnimeRatingSummaryUseCase
import su.afk.yummy.tv.domain.account.usecase.GetAnimeUserRatingUseCase
import su.afk.yummy.tv.domain.account.usecase.SetAnimeRatingUseCase
import javax.inject.Inject

/** Loads and mutates a title's rating, keeping fetch/mutation side-effects out of the ViewModel. */
internal class RatingMutationHandler @Inject constructor(
    private val getAnimeRatingSummary: GetAnimeRatingSummaryUseCase,
    private val getAnimeListStats: GetAnimeListStatsUseCase,
    private val getAnimeUserRating: GetAnimeUserRatingUseCase,
    private val setAnimeRating: SetAnimeRatingUseCase,
    private val deleteAnimeRating: DeleteAnimeRatingUseCase,
) {
    suspend fun load(animeId: Int): RatingLoadResult = coroutineScope {
        val ratingSummary = async { runSuspendCatching { getAnimeRatingSummary(animeId) } }
        val listStats = async { runSuspendCatching { getAnimeListStats(animeId) } }
        val userRating = async { runSuspendCatching { getAnimeUserRating(animeId) } }
        RatingLoadResult(
            ratingSummary = ratingSummary.await(),
            listStats = listStats.await(),
            userRating = userRating.await(),
        )
    }

    suspend fun setRating(animeId: Int, rating: Int): RatingMutationResult =
        runCatching { setAnimeRating(animeId, rating) }.toMutationResult()

    suspend fun deleteRating(animeId: Int): RatingMutationResult =
        runCatching { deleteAnimeRating(animeId) }.toMutationResult()

    suspend fun refreshSummary(animeId: Int): AnimeRatingSummary? =
        runCatching { getAnimeRatingSummary(animeId) }.getOrNull()

    private fun Result<*>.toMutationResult(): RatingMutationResult =
        if (isSuccess) RatingMutationResult.Success else RatingMutationResult.Failure
}

/** Partial-success outcome of loading rating summary, list stats and the current user rating. */
internal data class RatingLoadResult(
    val ratingSummary: Result<AnimeRatingSummary>,
    val listStats: Result<AnimeListStats>,
    val userRating: Result<Int?>,
)

/** Outcome of a rating mutation (set or delete). */
internal sealed interface RatingMutationResult {
    data object Success : RatingMutationResult
    data object Failure : RatingMutationResult
}
