package su.afk.yummy.tv.feature.details.rating.utils

import su.afk.yummy.tv.domain.account.model.AnimeListStats
import su.afk.yummy.tv.domain.account.model.AnimeRatingBucket
import su.afk.yummy.tv.domain.account.model.UserAnimeList
import java.util.Locale

internal fun AnimeListStats.count(list: UserAnimeList): Int = counts[list.id] ?: 0

internal fun List<AnimeRatingBucket>.weightedAverage(): Double? {
    val total = sumOf { it.count }
    if (total <= 0) return null
    val weighted = sumOf { it.rating.toDouble() * it.count.toDouble() }
    return weighted / total.toDouble()
}

internal fun Double.formatRating(): String = String.format(Locale.US, "%.1f", this)
