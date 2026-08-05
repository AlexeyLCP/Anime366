package su.afk.yummy.tv.feature.library.utils

import androidx.compose.ui.graphics.Color
import su.afk.yummy.tv.core.designsystem.presenter.theme.YummySemanticColors
import su.afk.yummy.tv.feature.library.LibraryTab

/** Цвет статусных вкладок; null для CONTINUE_WATCHING/HISTORY — им нужен MaterialTheme-цвет. */
fun LibraryTab.semanticColorOrNull(): Color? = when (this) {
    LibraryTab.CONTINUE_WATCHING, LibraryTab.HISTORY -> null
    LibraryTab.WATCHING -> YummySemanticColors.StatusWatching
    LibraryTab.PLANNED -> YummySemanticColors.StatusPlanned
    LibraryTab.COMPLETED -> YummySemanticColors.StatusCompleted
    LibraryTab.POSTPONED -> YummySemanticColors.StatusPostponed
    LibraryTab.DROPPED -> YummySemanticColors.StatusDropped
    LibraryTab.FAVORITES -> YummySemanticColors.StatusFavorite
}
