package su.afk.yummy.tv.core.model.settings

enum class DetailsButtonAction {
    WATCH,
    LIBRARY,
    FAVORITE,
    EPISODES,
    SUBSCRIPTIONS,
    FULL_DETAILS,
    TRAILERS,
    SIMILAR,
    VIEWING_ORDER,
    RATING,
    COLLECTIONS,
    COMMENTS,
    REVIEWS,
    BLOGGER_VIDEOS,
    SCREENSHOTS,
    ;

    val isAvailableOnAnime365: Boolean
        get() = when (this) {
            SUBSCRIPTIONS,
            TRAILERS,
            RATING,
            COLLECTIONS,
            COMMENTS,
            REVIEWS,
            BLOGGER_VIDEOS,
            -> false
            else -> true
        }
}
