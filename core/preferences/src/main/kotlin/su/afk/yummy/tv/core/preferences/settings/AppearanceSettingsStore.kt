package su.afk.yummy.tv.core.preferences.settings

import kotlinx.coroutines.flow.Flow
import su.afk.yummy.tv.core.model.settings.AppTheme
import su.afk.yummy.tv.core.model.settings.BackgroundStyle
import su.afk.yummy.tv.core.model.settings.DetailsButtonAction
import su.afk.yummy.tv.core.model.settings.LibraryContinueWatchingCardSize
import su.afk.yummy.tv.core.model.settings.LibrarySort
import su.afk.yummy.tv.core.model.settings.LibrarySortDirection
import su.afk.yummy.tv.core.model.settings.PosterCardSize
import su.afk.yummy.tv.core.model.settings.PosterQuality

/** Оформление интерфейса: тема, постеры, порядок и размеры карточек. */
interface AppearanceSettingsStore {

    val posterQuality: Flow<PosterQuality>
    val posterCardSize: Flow<PosterCardSize>
    val showTopTitleYear: Flow<Boolean>
    val showLibraryTitleYear: Flow<Boolean>
    val libraryContinueWatchingCardSize: Flow<LibraryContinueWatchingCardSize>
    val librarySort: Flow<LibrarySort>
    val librarySortDirection: Flow<LibrarySortDirection>
    val appTheme: Flow<AppTheme>
    val backgroundStyle: Flow<BackgroundStyle>
    val detailsButtonOrder: Flow<List<DetailsButtonAction>>

    suspend fun setPosterQuality(quality: PosterQuality)
    suspend fun setPosterCardSize(size: PosterCardSize)
    suspend fun setShowTopTitleYear(enabled: Boolean)
    suspend fun setShowLibraryTitleYear(enabled: Boolean)
    suspend fun setLibraryContinueWatchingCardSize(size: LibraryContinueWatchingCardSize)
    suspend fun setLibrarySort(sort: LibrarySort)
    suspend fun setLibrarySortDirection(direction: LibrarySortDirection)
    suspend fun setAppTheme(theme: AppTheme)
    suspend fun setBackgroundStyle(style: BackgroundStyle)
    suspend fun setDetailsButtonOrder(order: List<DetailsButtonAction>)

    companion object {
        val defaultDetailsButtonOrder: List<DetailsButtonAction> =
            DetailsButtonAction.entries.filter { it.isAvailableOnAnime365 }
    }
}
