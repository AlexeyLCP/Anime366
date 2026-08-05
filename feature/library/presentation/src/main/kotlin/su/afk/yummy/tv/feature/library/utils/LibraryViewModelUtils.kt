package su.afk.yummy.tv.feature.library.utils

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableMap
import su.afk.yummy.tv.domain.account.model.UserAnimeList
import su.afk.yummy.tv.domain.library.model.LibraryItem
import su.afk.yummy.tv.feature.library.LibraryTab

internal fun LibraryTab.userAnimeList(): UserAnimeList? = when (this) {
    LibraryTab.WATCHING -> UserAnimeList.WATCHING
    LibraryTab.PLANNED -> UserAnimeList.PLANNED
    LibraryTab.COMPLETED -> UserAnimeList.COMPLETED
    LibraryTab.POSTPONED -> UserAnimeList.POSTPONED
    LibraryTab.DROPPED -> UserAnimeList.DROPPED
    LibraryTab.CONTINUE_WATCHING,
    LibraryTab.HISTORY,
    LibraryTab.FAVORITES -> null
}

/** Раскладывает элементы библиотеки по вкладкам, чтобы UI не занимался фильтрацией. */
internal fun buildLibraryTabItems(
    items: List<LibraryItem>,
): ImmutableMap<LibraryTab, ImmutableList<LibraryItem>> =
    LibraryTab.visibleEntries.associateWith { tab ->
        when (tab) {
            LibraryTab.CONTINUE_WATCHING, LibraryTab.HISTORY -> emptyList()
            LibraryTab.FAVORITES -> items.filter { it.isFavorite }
            LibraryTab.WATCHING,
            LibraryTab.PLANNED,
            LibraryTab.COMPLETED,
            LibraryTab.POSTPONED,
            LibraryTab.DROPPED -> {
                val localListId = tab.userAnimeList()?.id
                items.filter { it.listId == localListId }
            }
        }.toImmutableList()
    }.toImmutableMap()

internal fun Long.toToastTimeString(): String {
    val totalSeconds = coerceAtLeast(0L) / 1_000L
    val hours = totalSeconds / 3_600L
    val minutes = (totalSeconds % 3_600L) / 60L
    val seconds = totalSeconds % 60L
    return if (hours > 0) {
        "%d:%02d:%02d".format(hours, minutes, seconds)
    } else {
        "%d:%02d".format(minutes, seconds)
    }
}
