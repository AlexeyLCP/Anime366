package su.afk.yummy.tv.feature.library.model

enum class LibraryTab {
    CONTINUE_WATCHING,
    HISTORY,
    FAVORITES,
    WATCHING,
    PLANNED,
    COMPLETED,
    POSTPONED,
    DROPPED,

    ;

    companion object {
        val visibleEntries: List<LibraryTab> = entries.filterNot { it == HISTORY }
    }
}
