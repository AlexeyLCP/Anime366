package su.afk.yummy.tv.core.deeplink

import android.content.Intent
import su.afk.yummy.tv.core.deeplink.api.DeepLinkHandler
import su.afk.yummy.tv.core.deeplink.api.DeepLinkReference
import su.afk.yummy.tv.core.deeplink.api.DeepLinkResolver
import su.afk.yummy.tv.core.navigation.manager.INavigationManager
import javax.inject.Inject

internal class DeepLinkHandlerImpl @Inject constructor(
    private val resolvers: Set<@JvmSuppressWildcards DeepLinkResolver>,
    private val navManager: INavigationManager,
) : DeepLinkHandler {

    override fun handle(intent: Intent) {
        val uri = intent.data ?: return
        val link = DeepLinkReference(intent = intent, uri = uri)
        resolvers.firstNotNullOfOrNull { it.resolve(link) }?.let(navManager::navigate)
    }
}
