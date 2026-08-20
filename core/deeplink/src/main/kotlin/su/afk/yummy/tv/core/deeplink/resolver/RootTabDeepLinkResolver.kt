package su.afk.yummy.tv.core.deeplink.resolver

import androidx.navigation3.runtime.NavKey
import su.afk.yummy.tv.core.deeplink.api.DeepLinkReference
import su.afk.yummy.tv.core.deeplink.api.DeepLinkResolver
import su.afk.yummy.tv.core.navigation.manager.INavigationManager
import su.afk.yummy.tv.core.navigation.root.RootTab
import javax.inject.Inject

/** Разбирает ссылки на корневые вкладки приложения (`yummytv://home`). */
internal class RootTabDeepLinkResolver @Inject constructor(
    private val navManager: INavigationManager,
) : DeepLinkResolver {

    override fun resolve(link: DeepLinkReference): NavKey? = when (link.appLinkHost) {
        "home" -> navManager.roots[RootTab.HOME]
        else -> null
    }
}
