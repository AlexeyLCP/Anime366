package su.afk.yummy.tv.feature.player.mobile.navigator

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import su.afk.yummy.tv.core.designsystem.baseScreen.ScreenNavigator
import su.afk.yummy.tv.core.navigation.manager.INavigationManager
import su.afk.yummy.tv.core.navigation.registrar.NavRegistrar
import su.afk.yummy.tv.feature.player.PlayerViewModel
import su.afk.yummy.tv.feature.player.mobile.PlayerMobileScreen
import su.afk.yummy.tv.feature.player.navigator.PlayerDestination
import javax.inject.Inject

class PlayerNavRegistrar @Inject constructor() : NavRegistrar {
    override fun register(builder: EntryProviderScope<NavKey>, nav: INavigationManager) =
        with(builder) {
            entry<PlayerDestination> { dest ->
                val viewModel = hiltViewModel<PlayerViewModel, PlayerViewModel.Factory>(
                    key = "player",
                    creationCallback = { factory -> factory.create(dest) },
                )
                ScreenNavigator(viewModel) { state, effect, onEvent ->
                    PlayerMobileScreen(
                        dest = dest,
                        state = state,
                        effect = effect,
                        onEvent = onEvent
                    )
                }
            }
        }
}
