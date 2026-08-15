package su.afk.yummy.tv.data.player.extractor.alloha

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.webkit.CookieManager
import android.webkit.JavascriptInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import su.afk.yummy.tv.core.analytics.api.AnalyticsTracker
import su.afk.yummy.tv.core.utils.coroutines.ioScope
import su.afk.yummy.tv.data.player.extractor.SessionAwarePlayerStreamExtractor
import su.afk.yummy.tv.data.player.extractor.common.logExtractorFailure
import su.afk.yummy.tv.domain.player.isAllohaPlayerUrl
import su.afk.yummy.tv.domain.player.model.AllohaStreamSession
import su.afk.yummy.tv.domain.player.model.PlayerStreamRequest
import su.afk.yummy.tv.domain.player.model.PlayerStreamResolveResult
import java.net.URL
import java.util.Locale
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.random.Random

/** Extracts Alloha's signed HLS session by observing the iframe's own network stack. */
internal class AllohaExtractor @Inject constructor(
    private val analyticsTracker: AnalyticsTracker,
) : SessionAwarePlayerStreamExtractor {
    private val extractorScope = ioScope()
    private val webViewPool = AllohaWebViewPool()

    override fun supports(url: String): Boolean = url.isAllohaPlayerUrl()

    override suspend fun extract(
        request: PlayerStreamRequest,
        context: Context,
    ): PlayerStreamResolveResult = withContext(Dispatchers.Main) {
        when (
            val result = openSessionViaWebView(
                iframeUrl = request.iframeUrl,
                preferredQualityLabel = request.autoQualityLabel,
                fallbackTtlSeconds = request.sessionFallbackTtlSeconds,
                context = context,
            )
        ) {
            is AllohaOpenResult.Unavailable -> PlayerStreamResolveResult.Unavailable(result.message)
            AllohaOpenResult.Failed -> PlayerStreamResolveResult.Failed
            is AllohaOpenResult.Ready -> {
                val session = result.session
                try {
                    (session as? LiveAllohaStreamSession)?.directStream ?: session.initialStream
                } finally {
                    session.close()
                }
            }
        }
    }

    override suspend fun openSession(
        request: PlayerStreamRequest,
        context: Context,
    ): AllohaStreamSession? = withContext(Dispatchers.Main) {
        (
                openSessionViaWebView(
                    iframeUrl = request.iframeUrl,
                    preferredQualityLabel = request.autoQualityLabel,
                    fallbackTtlSeconds = request.sessionFallbackTtlSeconds,
                    context = context,
                ) as? AllohaOpenResult.Ready
                )?.session
    }

    private sealed interface AllohaOpenResult {
        data class Ready(val session: AllohaStreamSession) : AllohaOpenResult
        data class Unavailable(val message: String?) : AllohaOpenResult
        data object Failed : AllohaOpenResult
    }

    @SuppressLint("SetJavaScriptEnabled", "AddJavascriptInterface")
    private suspend fun openSessionViaWebView(
        iframeUrl: String,
        preferredQualityLabel: String?,
        fallbackTtlSeconds: Int?,
        context: Context,
    ): AllohaOpenResult = suspendCancellableCoroutine { continuation ->
        val handler = Handler(Looper.getMainLooper())
        var delivered = false
        var streamReady = false
        var refreshedMasterReady = false
        var pendingHostChangeMaster: String? = null
        val liveSession = LiveAllohaStreamSession(handler, iframeUrl)
        val hostChangeFallback = Runnable {
            if (pendingHostChangeMaster != null) {
                // No fresh config_update confirmed the new host in time - the held headers are
                // still signed for the OLD host, so applying them would likely just 403. Force a
                // full session restart instead, same as the reference implementation does here.
                pendingHostChangeMaster = null
                Log.w(LOG_TAG, "host-change config_update timed out, forcing session restart")
                liveSession.refresh()
            }
        }
        lateinit var timeout: Runnable
        val masterWaitTimeout = Runnable {
            if (!delivered && streamReady) {
                Log.w(LOG_TAG, "refreshed master timeout, using captured quality playlist")
                liveSession.startProxy()
                delivered = true
                handler.removeCallbacks(timeout)
                if (continuation.isActive) continuation.resume(AllohaOpenResult.Ready(liveSession))
            }
        }

        fun deliverWhenReady() {
            if (delivered || !streamReady || !refreshedMasterReady) return
            liveSession.startProxy()
            delivered = true
            handler.removeCallbacks(timeout)
            handler.removeCallbacks(masterWaitTimeout)
            if (continuation.isActive) continuation.resume(AllohaOpenResult.Ready(liveSession))
        }

        fun fail(reason: AllohaOpenResult = AllohaOpenResult.Failed) {
            if (delivered) return
            delivered = true
            handler.removeCallbacksAndMessages(null)
            liveSession.close()
            if (continuation.isActive) continuation.resume(reason)
        }

        timeout = Runnable {
            analyticsTracker.logExtractorFailure(
                "Alloha",
                iframeUrl,
                "timed out waiting for signed HLS session"
            )
            fail()
        }
        handler.postDelayed(timeout, TIMEOUT_MS)

        val bridge = object {
            @JavascriptInterface
            fun onReady(responseJson: String, headersJson: String) {
                extractorScope.launch {
                    val parsed = runCatching {
                        Pair(
                            parseResult(responseJson, headersJson, preferredQualityLabel),
                            parseHeaders(headersJson),
                        )
                    }
                    handler.post {
                        parsed.onSuccess { (stream, headers) ->
                            CookieManager.getInstance().flush()
                            liveSession.initialize(stream)
                            liveSession.updateHeaders(headers)
                            fallbackTtlSeconds?.let(liveSession::ensureFallbackExpiry)
                            streamReady = true
                            Log.i(LOG_TAG, "ready headers=${liveSession.safeHeaderState()}")
                            deliverWhenReady()
                            if (!delivered) {
                                handler.removeCallbacks(masterWaitTimeout)
                                handler.postDelayed(masterWaitTimeout, MASTER_WAIT_TIMEOUT_MS)
                            }
                        }.onFailure {
                            analyticsTracker.logExtractorFailure(
                                "Alloha",
                                iframeUrl,
                                it.message ?: "invalid response"
                            )
                            if (it is AllohaSourceUnavailableException) {
                                // it.message is an internal debug reason (already logged above),
                                // not user-facing text - the presentation layer supplies that.
                                fail(AllohaOpenResult.Unavailable(message = null))
                            } else {
                                fail()
                            }
                        }
                    }
                }
            }

            @JavascriptInterface
            fun onConfigUpdate(edgeHash: String, ttlSeconds: Int, headersJson: String) {
                handler.post {
                    liveSession.updateHeaders(parseHeaders(headersJson) + ("accepts-controls" to edgeHash))
                    liveSession.updateExpiry(ttlSeconds)
                    val pendingMaster = pendingHostChangeMaster
                    if (pendingMaster != null) {
                        // The new edge_hash we just applied above is now current for the new
                        // host - safe to commit the master URL that was waiting on it.
                        handler.removeCallbacks(hostChangeFallback)
                        liveSession.updateMasterUrl(pendingMaster)
                        pendingHostChangeMaster = null
                        Log.i(LOG_TAG, "host change confirmed by fresh config_update")
                    }
                    Log.i(
                        LOG_TAG,
                        "config ttl=$ttlSeconds headers=${liveSession.safeHeaderState()}"
                    )
                }
            }

            @JavascriptInterface
            fun onM3u8Refreshed(url: String, headersJson: String) {
                extractorScope.launch {
                    val headers = parseHeaders(headersJson)
                    val masterUrl = url.normalizeStreamUrl()
                    handler.post {
                        val previousHost = liveSession.currentMasterUrl().hostOrNull()
                        val newHost = masterUrl.hostOrNull()
                        // Headers merge in regardless (they may carry a still-useful token); only
                        // the master URL itself is held back below when the host changed.
                        liveSession.updateHeaders(headers)
                        if (liveSession.isRotating) {
                            // A staged rotation already gets this guarantee for free: its master and
                            // its edge_hash are applied to the live state in one step, and the
                            // previous token keeps serving until then. Holding the master back here
                            // would only stall the commit.
                            liveSession.updateMasterUrl(masterUrl)
                            Log.i(
                                LOG_TAG,
                                "master refreshed for staged rotation host=$newHost",
                            )
                        } else if (refreshedMasterReady && previousHost != null && newHost != null &&
                            previousHost != newHost
                        ) {
                            // CDN node switched mid-session: the accepts-controls token we're
                            // still holding is signed for the OLD host and would 403 on the new
                            // one. Hold the master URL update until a fresh config_update confirms
                            // the new token, falling back to a full restart if none arrives.
                            pendingHostChangeMaster = masterUrl
                            handler.removeCallbacks(hostChangeFallback)
                            handler.postDelayed(hostChangeFallback, HOST_CHANGE_CONFIG_WAIT_MS)
                            Log.w(
                                LOG_TAG,
                                "master host changed $previousHost -> $newHost, awaiting fresh config_update",
                            )
                        } else {
                            liveSession.updateMasterUrl(masterUrl)
                            refreshedMasterReady = true
                            Log.i(
                                LOG_TAG,
                                "master refreshed headers=${liveSession.safeHeaderState()}"
                            )
                            deliverWhenReady()
                        }
                    }
                }
            }

            @JavascriptInterface
            fun onStreamHeaders(headersJson: String) {
                handler.post {
                    CookieManager.getInstance().flush()
                    liveSession.updateHeaders(parseHeaders(headersJson))
                }
            }

            @JavascriptInterface
            fun onDubbingUnavailable() {
                handler.post {
                    analyticsTracker.logExtractorFailure(
                        "Alloha",
                        iframeUrl,
                        "site rendered a dubbing-unavailable message",
                    )
                    fail(AllohaOpenResult.Unavailable(message = null))
                }
            }

            @JavascriptInterface
            fun onLog(message: String) {
                Log.d(LOG_TAG, "WebView session: $message")
            }
        }

        val userAgent = desktopUserAgent()
        val parsedUrl = URL(iframeUrl)
        val baseUrl = "${parsedUrl.protocol}://${parsedUrl.host.lowercase(Locale.ROOT)}/"
        val html = wrapperHtml(iframeUrl)
        val webView = webViewPool.acquire(context, userAgent).apply {
            removeJavascriptInterface(BRIDGE_NAME)
            addJavascriptInterface(bridge, BRIDGE_NAME)

            // The WebView is never attached to a window (extraction is headless, and for downloads
            // it runs in a background worker), so without this the browser throttles its JS timers
            // and pauses media playback - the iframe player then never fetches its correctly-signed
            // master.m3u8 (onM3u8Refreshed) and we fall back to the bnsi URL that 403s. onResume +
            // resumeTimers keep the offscreen player running, same as the reference implementation.
            onResume()
            resumeTimers()
            loadDataWithBaseURL(baseUrl, html, "text/html", "UTF-8", null)
        }
        liveSession.attach(
            webView = webView,
            refresh = {
                // A WebView reload rotates signed session data, but the browser fingerprint must
                // stay stable for the lifetime of this Alloha session.
                webView.settings.userAgentString = userAgent
                webView.loadDataWithBaseURL(baseUrl, html, "text/html", "UTF-8", null)
            },
            release = webViewPool::release,
        )

        continuation.invokeOnCancellation { handler.post { liveSession.close() } }
    }

    private fun String.hostOrNull(): String? =
        runCatching { URL(this).host }.getOrNull()?.takeIf(String::isNotBlank)

    private fun desktopUserAgent(): String {
        val os = DESKTOP_OS.random()
        val version = Random.nextInt(130, 136)
        return "Mozilla/5.0 ($os) AppleWebKit/537.36 (KHTML, like Gecko) " +
                "Chrome/$version.0.0.0 Safari/537.36"
    }

    private companion object {
        const val LOG_TAG = "AllohaExtractor"
        val DESKTOP_OS = listOf(
            "Windows NT 10.0; Win64; x64",
            "Windows NT 11.0; Win64; x64",
            "Macintosh; Intel Mac OS X 10_15_7",
            "Macintosh; Intel Mac OS X 14_4_1",
            "X11; Linux x86_64",
            "X11; Ubuntu; Linux x86_64",
        )
        const val TIMEOUT_MS = 30_000L

        // How long to wait for the iframe's own master.m3u8 (onM3u8Refreshed) before falling back
        // to the raw bnsi quality URL. The bnsi URL carries a path token the CDN rejects with 403
        // token_decrypt once the live session is established; only the master the iframe itself
        // fetches is signed correctly, so the fallback must stay a genuine last resort. The wrapper
        // JS keeps the iframe player actively playing (even after the session is captured) so it
        // reliably (re)fetches that master within a couple of seconds; this window just needs a
        // little headroom over that.
        const val MASTER_WAIT_TIMEOUT_MS = 6_000L
        const val HOST_CHANGE_CONFIG_WAIT_MS = 10_000L
    }
}
