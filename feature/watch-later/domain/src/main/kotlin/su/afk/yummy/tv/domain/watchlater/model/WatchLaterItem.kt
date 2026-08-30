package su.afk.yummy.tv.domain.watchlater.model

/**
 * Отложенная серия. Метаданные тайтла лежат рядом с ключом — список локальный и должен
 * рисоваться без похода в сеть.
 */
data class WatchLaterItem(
    val animeId: Int,
    val episode: String,
    val animeTitle: String,
    val posterUrl: String,
    val screenshotUrl: String,
    val addedAt: Long,
)
