package su.afk.yummy.tv.core.model.anime

/**
 * Сезон выхода тайтла (квартал года). [slug] совпадает со значениями фильтра `season`
 * в поиске yani, поэтому его же храним в базе — порядок констант в enum'е менять безопасно.
 */
enum class AnimeSeason(val slug: String) {
    WINTER("winter"),
    SPRING("spring"),
    SUMMER("summer"),
    FALL("fall"),
    ;

    companion object {
        fun fromSlug(slug: String?): AnimeSeason? {
            val normalized = slug?.trim()?.lowercase() ?: return null
            return entries.firstOrNull { it.slug == normalized }
        }

        /** Нумерация yani: 1 — зима, 2 — весна, 3 — лето, 4 — осень. */
        fun fromNumber(number: Int?): AnimeSeason? = entries.getOrNull((number ?: return null) - 1)
    }
}
