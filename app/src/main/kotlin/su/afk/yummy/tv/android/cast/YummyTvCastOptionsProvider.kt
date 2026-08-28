package su.afk.yummy.tv.android.cast

import android.content.Context
import com.google.android.gms.cast.CastMediaControlIntent
import com.google.android.gms.cast.framework.CastOptions
import com.google.android.gms.cast.framework.OptionsProvider
import com.google.android.gms.cast.framework.SessionProvider

/**
 * Медиа без DRM (Alloha/Kodik отдают обычный HLS/MP4), поэтому вместо
 * [androidx.media3.cast.DefaultCastOptionsProvider] (App ID A12D4273 - Default Media Receiver
 * with DRM support) используется классический публичный ресивер CC1AD845: часть Cast-устройств
 * (в т.ч. "Chromecast built-in" телевизоры) не регистрируют поддержку DRM-ресивера и не попадают
 * в отфильтрованный по App ID список при discovery, хотя системный Cast их видит.
 */
class YummyTvCastOptionsProvider : OptionsProvider {

    override fun getCastOptions(context: Context): CastOptions =
        CastOptions.Builder()
            .setResumeSavedSession(false)
            .setEnableReconnectionService(false)
            .setReceiverApplicationId(CastMediaControlIntent.DEFAULT_MEDIA_RECEIVER_APPLICATION_ID)
            .setStopReceiverApplicationWhenEndingSession(true)
            .setRemoteToLocalEnabled(true)
            .build()

    override fun getAdditionalSessionProviders(context: Context): List<SessionProvider>? = null
}
