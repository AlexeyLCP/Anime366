package su.afk.yummy.tv.core.deeplink.api

import android.content.Intent
import android.net.Uri

/**
 * Внешнее обращение к приложению: разобранный [Intent] и его [Uri].
 * Передаётся резолверам вместо голого Intent, чтобы каждому не приходилось повторять
 * проверку `intent.data != null`.
 */
data class DeepLinkReference(
    val intent: Intent,
    val uri: Uri,
) {

    /**
     * Хост собственной ссылки приложения (`yummytv://<host>/...`) или null, если это ссылка
     * другой схемы. Избавляет резолверы от дублирования проверки схемы.
     */
    val appLinkHost: String?
        get() = if (uri.scheme == APP_SCHEME) uri.host else null

    private companion object {
        const val APP_SCHEME = "yummytv"
    }
}
