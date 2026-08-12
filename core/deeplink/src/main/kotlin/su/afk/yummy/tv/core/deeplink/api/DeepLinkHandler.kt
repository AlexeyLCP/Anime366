package su.afk.yummy.tv.core.deeplink.api

import android.content.Intent

interface DeepLinkHandler {
    fun handle(intent: Intent)
}
