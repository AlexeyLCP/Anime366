package su.afk.yummy.tv.core.tv.api

/**
 * Карточка для системного preview-канала Android TV. Плоская проекция контента: [TvChannelContent]
 * описывает ровно то, что умеет показать `TvContractCompat.PreviewPrograms`, и не знает, из какой
 * фичи пришли данные.
 */
data class TvChannelContent(
    val animeId: Int,
    val title: String,
    val posterUrl: String?,
)
