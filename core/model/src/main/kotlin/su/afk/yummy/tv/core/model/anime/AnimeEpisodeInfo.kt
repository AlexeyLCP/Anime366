package su.afk.yummy.tv.core.model.anime

/** Название и описание серии из YummyTV API (данные TMDB). */
data class AnimeEpisodeInfo(
    val number: Int,
    val title: String?,
    val description: String?,
)
