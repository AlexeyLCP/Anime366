package su.afk.yummy.tv.core.deeplink

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import dagger.hilt.android.qualifiers.ApplicationContext
import su.afk.yummy.tv.core.navigation.NavigationManager
import su.afk.yummy.tv.feature.player.IPlayerNavigator
import javax.inject.Inject

interface DeepLinkHandler {
    fun handle(intent: Intent)
}

internal class DeepLinkHandlerImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val resolver: DeepLinkResolver,
    private val navManager: NavigationManager,
    private val playerNavigator: IPlayerNavigator,
) : DeepLinkHandler {

    override fun handle(intent: Intent) {
        val uri = intent.data ?: return
        // Внешнее открытие локального видеофайла (ACTION_VIEW + content://video/*):
        // такие Intent приходят из файловых менеджеров/галереи через «Открыть с помощью».
        val isLocalVideo = intent.action == Intent.ACTION_VIEW &&
                (intent.type?.startsWith("video/") == true ||
                        uri.scheme == ContentResolver.SCHEME_CONTENT ||
                        uri.scheme == ContentResolver.SCHEME_FILE)
        if (isLocalVideo) {
            navManager.navigate(
                playerNavigator.getLocalFilePlayerDest(uri.toString(), title = uri.displayName())
            )
            return
        }
        resolver.resolve(uri)?.let(navManager::navigate)
    }

    /**
     * Читаемое имя файла для заголовка плеера: DISPLAY_NAME для content://,
     * последний сегмент пути для file://. Сбой запроса не должен ломать
     * воспроизведение, поэтому оборачиваем в runCatching и на ошибке отдаём "".
     */
    private fun Uri.displayName(): String {
        val raw = when (scheme) {
            ContentResolver.SCHEME_CONTENT -> runCatching {
                context.contentResolver.query(
                    this,
                    arrayOf(OpenableColumns.DISPLAY_NAME),
                    null,
                    null,
                    null,
                )?.use { cursor ->
                    val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (index >= 0 && cursor.moveToFirst()) cursor.getString(index) else null
                }
            }.getOrNull()

            else -> lastPathSegment
        }
        return raw?.substringBeforeLast('.').orEmpty()
    }
}
