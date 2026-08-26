package su.afk.yummy.tv.domain.search.model

enum class SearchSort(val apiValue: String?, val defaultForward: Boolean) {
    RELEVANCE(null, true),
    TITLE("title", false),
    YEAR("year", false),
    RATING("rating", false),
    RATING_COUNTERS("rating_counters", false),
    VIEWS("views", false),
    TOP("top", true),
    ID("id", false),
}
