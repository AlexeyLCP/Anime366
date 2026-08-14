package su.afk.yummy.tv.core.navigation.registrar

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import su.afk.yummy.tv.core.navigation.manager.INavigationManager

/**
 * Точка регистрации экранов фичи в общем `entryProvider`. Каждая фича реализует это через
 * Hilt multibinding (`@IntoSet`, см. [MobileUi]/[TvUi]).
 * `nav` типизирован интерфейсом [INavigationManager], а не конкретным `NavigationManager`
 * (тот `internal` в этом модуле) — фичам не нужен доступ к internal-wiring вроде
 * `attachBackStacks`, только навигационные операции.
 */
fun interface NavRegistrar {
    fun register(builder: EntryProviderScope<NavKey>, nav: INavigationManager)
}
