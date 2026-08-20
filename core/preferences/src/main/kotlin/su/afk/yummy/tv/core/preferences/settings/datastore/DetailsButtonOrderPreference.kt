package su.afk.yummy.tv.core.preferences.settings.datastore

import su.afk.yummy.tv.core.model.settings.DetailsButtonAction
import su.afk.yummy.tv.core.preferences.settings.SettingsStore

internal const val DETAILS_BUTTON_ORDER_SEPARATOR = "|"

/** Разбирает сохранённый порядок кнопок деталей, дополняя его недостающими действиями. */
internal fun String?.toDetailsButtonOrder(): List<DetailsButtonAction> {
    if (isNullOrBlank()) return SettingsStore.defaultDetailsButtonOrder
    return split(DETAILS_BUTTON_ORDER_SEPARATOR)
        .mapNotNull { name -> runCatching { DetailsButtonAction.valueOf(name) }.getOrNull() }
        .normalizedDetailsButtonOrder()
}

/**
 * Убирает дубли, дополняет порядок отсутствующими действиями и держит FAVORITE сразу
 * за LIBRARY — эта пара всегда показывается рядом.
 */
internal fun List<DetailsButtonAction>.normalizedDetailsButtonOrder(): List<DetailsButtonAction> {
    val unique = distinct()
    val complete = unique + SettingsStore.defaultDetailsButtonOrder.filterNot { it in unique }
    val withoutFavorite = complete.filterNot { it == DetailsButtonAction.FAVORITE }
    val libraryIndex = withoutFavorite.indexOf(DetailsButtonAction.LIBRARY)
    if (libraryIndex == -1) return complete
    return withoutFavorite.toMutableList().apply {
        add(libraryIndex + 1, DetailsButtonAction.FAVORITE)
    }
}
