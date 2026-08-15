package su.afk.yummy.tv.core.update.nav

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import su.afk.yummy.tv.core.designsystem.presenter.baseViewModel.ScreenNavigator
import su.afk.yummy.tv.core.navigation.manager.INavigationManager
import su.afk.yummy.tv.core.navigation.registrar.NavRegistrar
import su.afk.yummy.tv.core.update.UpdateViewModel
import javax.inject.Inject

class UpdateNavRegistrar @Inject constructor() : NavRegistrar {

    override fun register(builder: EntryProviderScope<NavKey>, nav: INavigationManager) =
        with(builder) {
            entry<UpdateDestination> { dest ->
                val viewModel = hiltViewModel<UpdateViewModel>()

                ScreenNavigator(viewModel) { state, _, onEvent ->
                    UpdateDialog(dest = dest, status = state.status, onEvent = onEvent)
                }
            }
        }
}
