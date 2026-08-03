package su.afk.yummy.tv.domain.account.model

val UserStats.topGenres: List<UserGenreStat>
    get() = genres.sortedByDescending { it.count }.take(8)

val UserStats.ratingsByValue: Map<Int, UserRatingStat>
    get() = ratings.associateBy { it.rating }
