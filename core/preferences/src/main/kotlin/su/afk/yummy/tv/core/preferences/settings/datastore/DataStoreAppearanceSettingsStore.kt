package su.afk.yummy.tv.core.preferences.settings.datastore

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import su.afk.yummy.tv.core.model.settings.AppTheme
import su.afk.yummy.tv.core.model.settings.BackgroundStyle
import su.afk.yummy.tv.core.model.settings.DetailsButtonAction
import su.afk.yummy.tv.core.model.settings.LibraryContinueWatchingCardSize
import su.afk.yummy.tv.core.model.settings.LibrarySort
import su.afk.yummy.tv.core.model.settings.LibrarySortDirection
import su.afk.yummy.tv.core.model.settings.PosterCardSize
import su.afk.yummy.tv.core.model.settings.PosterQuality
import su.afk.yummy.tv.core.preferences.settings.AppearanceSettingsStore
import su.afk.yummy.tv.core.preferences.settings.SettingsPreferenceKeys.appThemeKey
import su.afk.yummy.tv.core.preferences.settings.SettingsPreferenceKeys.backgroundStyleKey
import su.afk.yummy.tv.core.preferences.settings.SettingsPreferenceKeys.detailsButtonOrderKey
import su.afk.yummy.tv.core.preferences.settings.SettingsPreferenceKeys.libraryContinueWatchingCardSizeKey
import su.afk.yummy.tv.core.preferences.settings.SettingsPreferenceKeys.librarySortDirectionKey
import su.afk.yummy.tv.core.preferences.settings.SettingsPreferenceKeys.librarySortKey
import su.afk.yummy.tv.core.preferences.settings.SettingsPreferenceKeys.posterCardSizeKey
import su.afk.yummy.tv.core.preferences.settings.SettingsPreferenceKeys.posterQualityKey
import su.afk.yummy.tv.core.preferences.settings.SettingsPreferenceKeys.showLibraryTitleYearKey
import su.afk.yummy.tv.core.preferences.settings.SettingsPreferenceKeys.showTopTitleYearKey
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class DataStoreAppearanceSettingsStore @Inject constructor(
    private val store: SettingsDataStore,
) : AppearanceSettingsStore {

    override val posterQuality: Flow<PosterQuality> =
        store.enumFlow(posterQualityKey, defaultPosterQuality)

    override val posterCardSize: Flow<PosterCardSize> =
        store.enumFlow(posterCardSizeKey, PosterCardSize.STANDARD)

    override val showTopTitleYear: Flow<Boolean> = store.boolean(showTopTitleYearKey, false)

    override val showLibraryTitleYear: Flow<Boolean> =
        store.boolean(showLibraryTitleYearKey, false)

    override val libraryContinueWatchingCardSize: Flow<LibraryContinueWatchingCardSize> =
        store.enumFlow(libraryContinueWatchingCardSizeKey, LibraryContinueWatchingCardSize.LARGE)

    override val librarySort: Flow<LibrarySort> =
        store.enumFlow(librarySortKey, LibrarySort.ADDED_DATE)

    override val librarySortDirection: Flow<LibrarySortDirection> =
        store.enumFlow(librarySortDirectionKey, LibrarySortDirection.DESC)

    override val appTheme: Flow<AppTheme> = store.enumFlow(appThemeKey, AppTheme.WARM_AMBER)

    override val backgroundStyle: Flow<BackgroundStyle> =
        store.enumFlow(backgroundStyleKey, BackgroundStyle.DARK)

    override val detailsButtonOrder: Flow<List<DetailsButtonAction>> =
        store.data.map { prefs -> prefs[detailsButtonOrderKey].toDetailsButtonOrder() }

    override suspend fun setPosterQuality(quality: PosterQuality) =
        store.setEnum(posterQualityKey, quality)

    override suspend fun setPosterCardSize(size: PosterCardSize) =
        store.setEnum(posterCardSizeKey, size)

    override suspend fun setShowTopTitleYear(enabled: Boolean) =
        store.setBoolean(showTopTitleYearKey, enabled)

    override suspend fun setShowLibraryTitleYear(enabled: Boolean) =
        store.setBoolean(showLibraryTitleYearKey, enabled)

    override suspend fun setLibraryContinueWatchingCardSize(size: LibraryContinueWatchingCardSize) =
        store.setEnum(libraryContinueWatchingCardSizeKey, size)

    override suspend fun setLibrarySort(sort: LibrarySort) =
        store.setEnum(librarySortKey, sort)

    override suspend fun setLibrarySortDirection(direction: LibrarySortDirection) =
        store.setEnum(librarySortDirectionKey, direction)

    override suspend fun setAppTheme(theme: AppTheme) = store.setEnum(appThemeKey, theme)

    override suspend fun setBackgroundStyle(style: BackgroundStyle) =
        store.setEnum(backgroundStyleKey, style)

    override suspend fun setDetailsButtonOrder(order: List<DetailsButtonAction>) {
        store.edit { prefs ->
            prefs[detailsButtonOrderKey] = order.normalizedDetailsButtonOrder()
                .joinToString(DETAILS_BUTTON_ORDER_SEPARATOR) { it.name }
        }
    }

    internal companion object {
        val defaultPosterQuality: PosterQuality = PosterQuality.LOW
    }
}
