package su.afk.yummy.tv.feature.player.common.service

import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.analytics.AnalyticsListener
import androidx.media3.exoplayer.mediacodec.MediaCodecRenderer.DecoderInitializationException
import su.afk.yummy.tv.core.analytics.api.AnalyticsTracker
import su.afk.yummy.tv.core.analytics.utils.analyticsParamsOf

/**
 * Reports which video decoder Media3 actually ends up using, and any failed candidates along the
 * way. `player_error`/`player_stream_resolve_failed` only fire once every fallback candidate is
 * exhausted, so without this a hardware decoder that fails and silently recovers through
 * [androidx.media3.exoplayer.DefaultRenderersFactory.setEnableDecoderFallback] would be invisible
 * in analytics - see the Alloha decoder NO_MEMORY loop this was added to diagnose.
 */
@OptIn(UnstableApi::class)
internal class PlayerDecoderAnalyticsListener(
    private val tracker: AnalyticsTracker,
) : AnalyticsListener {

    // A format change can make MediaCodecRenderer walk several candidates before one succeeds
    // (or all fail). Counting the errors since the last successful init lets the initialized
    // event report whether it was the first candidate or a fallback.
    private var pendingVideoDecoderErrors = 0

    override fun onVideoCodecError(
        eventTime: AnalyticsListener.EventTime,
        videoCodecError: Exception,
    ) {
        pendingVideoDecoderErrors++
        val decoderName = (videoCodecError as? DecoderInitializationException)?.codecInfo?.name
        tracker.track(
            EVENT_PLAYER_DECODER_ERROR,
            analyticsParamsOf(
                PARAM_SCREEN to SCREEN_PLAYER,
                PARAM_DECODER_NAME to decoderName,
                PARAM_ERROR_TYPE to videoCodecError::class.java.simpleName,
            ),
        )
    }

    override fun onVideoDecoderInitialized(
        eventTime: AnalyticsListener.EventTime,
        decoderName: String,
        initializedTimestampMs: Long,
        initializationDurationMs: Long,
    ) {
        val fallback = pendingVideoDecoderErrors > 0
        pendingVideoDecoderErrors = 0
        tracker.track(
            EVENT_PLAYER_DECODER_INITIALIZED,
            analyticsParamsOf(
                PARAM_SCREEN to SCREEN_PLAYER,
                PARAM_DECODER_NAME to decoderName,
                PARAM_DECODER_HARDWARE to decoderName.isHardwareDecoderName(),
                PARAM_DECODER_FALLBACK to fallback,
                PARAM_DECODER_INIT_DURATION_MS to initializationDurationMs,
            ),
        )
    }

    private companion object {
        const val SCREEN_PLAYER = "player"
        const val PARAM_SCREEN = "screen"
        const val PARAM_ERROR_TYPE = "error_type"
        const val PARAM_DECODER_NAME = "decoder"
        const val PARAM_DECODER_HARDWARE = "decoder_hardware"
        const val PARAM_DECODER_FALLBACK = "decoder_fallback"
        const val PARAM_DECODER_INIT_DURATION_MS = "decoder_init_duration_ms"
        const val EVENT_PLAYER_DECODER_INITIALIZED = "player_decoder_initialized"
        const val EVENT_PLAYER_DECODER_ERROR = "player_decoder_error"

        // Best-effort: AOSP's software decoders are named "c2.android.*"/"OMX.google.*"; every
        // other name (vendor Codec2 components, or Google's own hardware-backed "c2.google.*" on
        // Pixel) is hardware-accelerated. Media3 has the exact answer via
        // MediaCodecInfo.hardwareAccelerated, but that flag isn't exposed on this callback -
        // only the decoder name is.
        fun String.isHardwareDecoderName(): Boolean =
            !startsWith("c2.android.") && !startsWith("OMX.google.")
    }
}
