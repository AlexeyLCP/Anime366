package su.afk.yummy.tv.data.player.extractor

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.webkit.CookieManager
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import org.json.JSONObject
import su.afk.yummy.tv.core.analytics.api.AnalyticsTracker
import su.afk.yummy.tv.core.utils.coroutines.ioScope
import su.afk.yummy.tv.domain.player.isAllohaPlayerUrl
import su.afk.yummy.tv.domain.player.model.AllohaStreamSession
import su.afk.yummy.tv.domain.player.model.PlayerStreamRequest
import su.afk.yummy.tv.domain.player.model.PlayerStreamResolveResult
import java.net.URL
import java.security.MessageDigest
import java.util.Locale
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.atomic.AtomicReference
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.random.Random

/** Extracts Alloha's signed HLS session by observing the iframe's own network stack. */
internal class AllohaExtractor @Inject constructor(
    private val analyticsTracker: AnalyticsTracker,
) : PlayerStreamExtractor {
    private val extractorScope = ioScope()

    // Idle WebViews kept warm for reuse - see acquireWebView()/releaseWebView() below.
    private val idleWebViews = ConcurrentLinkedQueue<WebView>()

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

    suspend fun openSession(
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
        val webView = acquireWebView(context, userAgent).apply {
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
            release = ::releaseWebView,
        )

        continuation.invokeOnCancellation { handler.post { liveSession.close() } }
    }

    private fun parseHeaders(headersJson: String): Map<String, String> {
        val objectValue = JSONObject(headersJson)
        return buildMap {
            objectValue.keys().forEach { key ->
                objectValue.optString(key).takeIf(String::isNotBlank)
                    ?.let { put(key.lowercase(), it) }
            }
        }
    }

    private fun parseResult(
        responseJson: String,
        headersJson: String,
        preferredQualityLabel: String?,
    ): PlayerStreamResolveResult.Stream {
        val headersObject = JSONObject(headersJson)
        val headers = buildMap {
            headersObject.keys().forEach { key -> put(key, headersObject.optString(key)) }
        }.filterValues(String::isNotBlank)

        val sources = JSONObject(responseJson).optJSONArray("hlsSource")
            ?: throw AllohaSourceUnavailableException("hlsSource is missing")
        Log.i(
            LOG_TAG,
            "bnsi hlsSources=${sources.length()} qualities=" +
                    (0 until sources.length()).map { index ->
                        sources.optJSONObject(index)?.optJSONObject("quality")?.keys()
                            ?.asSequence()?.toList().orEmpty()
                    },
        )
        val qualities = linkedMapOf<String, String>()
        for (index in 0 until sources.length()) {
            val quality = sources.optJSONObject(index)?.optJSONObject("quality") ?: continue
            quality.keys().forEach { label ->
                quality.optString(label)
                    .split(" or ")
                    .firstOrNull()
                    ?.trim()
                    ?.normalizeStreamUrl()
                    ?.takeIf(String::isNotBlank)
                    ?.let { qualities.putIfAbsent(label.normalizeQualityLabel(), it) }
            }
        }
        if (qualities.isEmpty()) throw AllohaSourceUnavailableException("no HLS qualities found")
        val sorted = qualities.entries
            .sortedBy { it.key.filter(Char::isDigit).toIntOrNull() ?: 0 }
            .associateTo(linkedMapOf()) { it.toPair() }
        val headersWithCookie = headers.toMutableMap().apply {
            sorted.values.firstNotNullOfOrNull { CookieManager.getInstance().getCookie(it) }
                ?.takeIf(String::isNotBlank)
                ?.let { put("Cookie", it) }
        }
        val preferredUrl = preferredQualityLabel
            ?.normalizeQualityLabel()
            ?.let(sorted::get)
        return PlayerStreamResolveResult.Stream(
            url = preferredUrl ?: sorted.values.last(),
            headers = headersWithCookie,
            qualities = sorted,
            qualityHeaders = sorted.keys.associateWith { headersWithCookie },
        )
    }

    // Booting a fresh WebView (and its underlying Chromium renderer) is the single most expensive
    // step in opening an Alloha session - a cold start that retries 2-3x (each retry tearing down
    // and recreating the WebView from scratch) pays that cost every time. The reference
    // implementation avoids this by keeping one WebView alive for the whole activity lifetime.
    // We use a small pool (capped at MAX_IDLE_WEB_VIEWS) instead of a single shared instance so a
    // second concurrent caller (e.g. a background download running while an episode is playing)
    // can't collide with an in-use WebView - it simply gets its own fresh instance, which it
    // destroys again on release instead of returning it to an already-full pool.
    @SuppressLint("SetJavaScriptEnabled")
    private fun acquireWebView(context: Context, userAgent: String): WebView {
        val webView = idleWebViews.poll() ?: WebView(context).apply {
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.mediaPlaybackRequiresUserGesture = false
            webViewClient = WebViewClient()
        }
        webView.settings.userAgentString = userAgent
        CookieManager.getInstance().apply {
            setAcceptCookie(true)
            setAcceptThirdPartyCookies(webView, true)
        }
        return webView
    }

    private fun releaseWebView(webView: WebView) {
        if (idleWebViews.size >= MAX_IDLE_WEB_VIEWS) {
            webView.destroy()
        } else {
            idleWebViews.offer(webView)
        }
    }

    private fun wrapperHtml(iframeUrl: String): String = """
        <html><body style="margin:0;background:black">
        <iframe id="alloha" src="${iframeUrl.escapeHtml()}" width="100%" height="100%" frameborder="0" allowfullscreen></iframe>
        <script>
        try {
          Object.defineProperty(document, 'visibilityState', {get:function(){return 'visible'}});
          Object.defineProperty(document, 'hidden', {get:function(){return false}});
        } catch(e) {}
        document.getElementById('alloha').onload = function() {
          try {
            var w = this.contentWindow, bnsi = null, headers = {}, done = false;
            try {
              Object.defineProperty(w.document, 'visibilityState', {get:function(){return 'visible'}});
              Object.defineProperty(w.document, 'hidden', {get:function(){return false}});
            } catch(e) {}
            var pushTimer = null;
            function put(k,v) {
              if(!k || !v) return;
              headers[String(k).toLowerCase()] = String(v);
              if(done) {
                if(pushTimer) clearTimeout(pushTimer);
                pushTimer = setTimeout(function(){ AndroidBridge.onStreamHeaders(JSON.stringify(headers)); }, 40);
              }
            }
            function ready() {
              if(done || !bnsi || !headers['authorizations'] || !headers['accepts-controls']) return;
              done = true;
              AndroidBridge.onReady(bnsi, JSON.stringify(headers));
            }
            put('origin', w.location.origin); put('referer', w.location.origin + '/');
            put('user-agent', w.navigator.userAgent); put('accept', '*/*');
            put('sec-fetch-dest', 'empty'); put('sec-fetch-mode', 'cors'); put('sec-fetch-site', 'cross-site');

            var lastMasterUrl = null;
            // Hand the proxy the correctly-signed master.m3u8 the player itself requests, even when
            // the browser blocks that request. The player fetches master with custom headers
            // (accepts-controls/authorizations) -> CORS preflight the CDN never answers -> the
            // request is blocked and 'load'/'ok' never fire, so only the requested URL is available.
            // Our server-side proxy isn't subject to CORS, so this up-to-date URL streams fine,
            // unlike the stale bnsi URL that the CDN 403s with token_decrypt.
            function isCdnMaster(url) {
              return !!url && url.indexOf('http') === 0 && url.indexOf('master.m3u8') !== -1;
            }
            function reportMaster(url) {
              if(!done || !url) return;
              if(!isCdnMaster(url)) return;
              if(url === lastMasterUrl) return;
              lastMasterUrl = url;
              AndroidBridge.onM3u8Refreshed(url, JSON.stringify(headers));
            }
            var primaryHost = null, fallbackHost = null, fallbackMasterUrl = null;
            function extractCdnHosts() {
              if(primaryHost || !bnsi) return;
              try {
                var data = JSON.parse(bnsi), sources = data.hlsSource;
                if(!sources || !sources[0] || !sources[0].quality) return;
                var quality = sources[0].quality, key = Object.keys(quality)[0];
                var urls = quality[key].split(' or ');
                if(urls.length < 2) return;
                var primary = urls[0].match(/https?:\/\/([^\/]+)/);
                var fallback = urls[1].trim().match(/https?:\/\/([^\/]+)/);
                if(primary) primaryHost = primary[1];
                if(fallback) {
                  fallbackHost = fallback[1];
                  fallbackMasterUrl = urls[1].trim();
                }
                AndroidBridge.onLog('CDN candidates captured');
              } catch(e) { AndroidBridge.onLog('CDN candidate parse failed'); }
            }

            var open = w.XMLHttpRequest.prototype.open;
            w.XMLHttpRequest.prototype.open = function(method,url) {
              this.__allohaUrl = url;
              this.addEventListener('load', function() {
                var url = this.responseURL || this.__allohaUrl || '';
                if(url.indexOf('/bnsi/') !== -1) {
                  bnsi = this.responseText;
                  extractCdnHosts();
                  ready();
                }
                reportMaster(url);
              });
              // Fallback capture: loadend fires on success and on failure. The send() hook below is
              // the primary capture point (and blocks CDN-master requests); this only covers any
              // request that reaches loadend without having passed through our send() override.
              this.addEventListener('loadend', function() {
                reportMaster(this.__allohaUrl || this.responseURL || '');
              });
              return open.apply(this, arguments);
            };
            var setHeader = w.XMLHttpRequest.prototype.setRequestHeader;
            w.XMLHttpRequest.prototype.setRequestHeader = function(k,v) {
              put(k,v); ready(); return setHeader.apply(this, arguments);
            };
            // The CDN token baked into the master path is single-use: whoever GETs it first wins,
            // the loser gets 403 token_decrypt. The player's own master XHR is native and already
            // in flight from this same JS tick, so it always beats our JSBridge->OkHttp proxy and
            // burns the token before the proxy can use it (its response is CORS-blocked from JS
            // anyway, so the player gains nothing from it). We therefore CAPTURE the master URL +
            // headers here but never let the request reach the network, leaving the token fresh for
            // the proxy - the sole consumer. A blocked XHR is failed asynchronously so the player's
            // error path still runs. Non-master XHRs (bnsi, config, etc.) pass through untouched.
            var xhrSend = w.XMLHttpRequest.prototype.send;
            w.XMLHttpRequest.prototype.send = function() {
              var u = this.__allohaUrl || '';
              if(isCdnMaster(u)) {
                reportMaster(u);
                var self = this;
                setTimeout(function() {
                  try {
                    self.dispatchEvent(new Event('error'));
                    self.dispatchEvent(new Event('loadend'));
                  } catch(e) {}
                }, 0);
                return;
              }
              reportMaster(u);
              return xhrSend.apply(this, arguments);
            };
            var fetch = w.fetch;
            w.fetch = function(input,init) {
              try {
                var url = typeof input === 'string' ? input : (input && input.url ? input.url : '');
                if(init && init.headers) {
                  if(typeof init.headers.forEach === 'function') init.headers.forEach(function(v,k){put(k,v)});
                  else for(var k in init.headers) put(k,init.headers[k]);
                }
                ready();
                extractCdnHosts();
                reportMaster(url);
                if(isCdnMaster(url)) {
                  // Same single-use-token reason as the XHR send() hook: capture but never fetch,
                  // so the token stays fresh for the proxy. Reject so the player's error path runs.
                  return Promise.reject(new TypeError('alloha: master fetch withheld to preserve CDN token'));
                }
                if(url && (url.indexOf('.m3u8') !== -1 || url.indexOf('.ts') !== -1 || url.indexOf('.m4s') !== -1) &&
                    primaryHost && fallbackHost && url.indexOf(primaryHost) !== -1) {
                  var fallbackUrl = url.indexOf('master.m3u8') !== -1 && fallbackMasterUrl
                    ? fallbackMasterUrl : url.replace(primaryHost, fallbackHost);
                  return fetch.apply(this, arguments).then(function(response) {
                    if(response.status !== 403 && response.status !== 500 && response.status !== 503) return response;
                    AndroidBridge.onLog('Browser CDN fallback after status=' + response.status);
                    return fetch.call(w, fallbackUrl, init).then(function(fallbackResponse) {
                      if(fallbackResponse.ok) reportMaster(fallbackUrl);
                      return fallbackResponse;
                    });
                  });
                }
              } catch(e) {}
              return fetch.apply(this, arguments);
            };

            var OrigWS = w.WebSocket, send = OrigWS.prototype.send;
            var heartbeat = null, started = Date.now(), activeSocket = null, lastEdgeHash = null;
            function startHeartbeat(socket) {
              if(heartbeat) clearInterval(heartbeat);
              started = Date.now();
              heartbeat = setInterval(function() {
                if(!done || !socket || socket.readyState !== 1) return;
                try {
                  send.call(socket, JSON.stringify({type:'playing',current_time:Math.floor((Date.now()-started)/1000),resolution:'1080',track_id:'1',speed:1,subtitle:0,ts:Date.now()}));
                  AndroidBridge.onLog('heartbeat sent');
                } catch(e) { AndroidBridge.onLog('heartbeat failed'); }
              }, 25000);
            }
            function hookSocket(socket) {
              if(!socket || socket.__allohaHooked) return socket;
              socket.__allohaHooked = true;
              activeSocket = socket;
              started = Date.now();
              AndroidBridge.onLog('WebSocket hooked');
              socket.addEventListener('message', function(event) {
                try {
                  var message = JSON.parse(event.data);
                  if(message && message.type === 'config_update' && message.edge_hash && message.edge_hash !== lastEdgeHash) {
                    lastEdgeHash = message.edge_hash;
                    put('accepts-controls', message.edge_hash); ready();
                    var ttl = message.ttl || 120;
                    AndroidBridge.onLog('config_update ttl=' + ttl);
                    AndroidBridge.onConfigUpdate(message.edge_hash, ttl, JSON.stringify(headers));
                  }
                } catch(e) {}
              });
              socket.addEventListener('open', function() {
                AndroidBridge.onLog('WebSocket opened');
                startHeartbeat(socket);
              });
              socket.addEventListener('close', function(event){
                if(activeSocket === socket) {
                  activeSocket = null;
                  if(heartbeat) clearInterval(heartbeat);
                }
                var reason = event && event.reason ? String(event.reason).replace(/\s+/g, ' ').slice(0, 80) : '';
                AndroidBridge.onLog('WebSocket closed code=' + (event ? event.code : 0) +
                  ' clean=' + (event ? event.wasClean : false) + ' reason=' + reason);
              });
              if(socket.readyState === 1) startHeartbeat(socket);
              return socket;
            }
            OrigWS.prototype.send = function(data) {
              hookSocket(this);
              return send.call(this,data);
            };
            w.WebSocket = function(url, protocols) {
              return hookSocket(protocols ? new OrigWS(url, protocols) : new OrigWS(url));
            };
            w.WebSocket.prototype = OrigWS.prototype;
            w.WebSocket.CONNECTING = OrigWS.CONNECTING;
            w.WebSocket.OPEN = OrigWS.OPEN;
            w.WebSocket.CLOSING = OrigWS.CLOSING;
            w.WebSocket.CLOSED = OrigWS.CLOSED;
            var errorReported = false;
            var unavailablePattern = /озвучка\s*недоступна/i;
            setInterval(function() {
              if(!done && !errorReported) {
                try {
                  var text = w.document.body ? w.document.body.textContent : '';
                  if(text && unavailablePattern.test(text)) {
                    errorReported = true;
                    AndroidBridge.onDubbingUnavailable();
                    return;
                  }
                } catch(e) {}
              }
              // Keep the iframe player actively playing even AFTER the session is captured. If we
              // stop at 'done', the player pauses and tears down its WebSocket (~2.4s) before it
              // ever fetches its correctly-signed master.m3u8 (onM3u8Refreshed), forcing us onto the
              // raw bnsi URL that the CDN rejects with 403 token_decrypt. Keeping it playing makes
              // the player keep (re)fetching its own master and keeps the socket alive for heartbeats.
              var button = w.document.querySelector('.allplay__play-btn'); if(button) button.click();
              var video = w.document.querySelector('video');
              if(video) { video.muted = true; if(video.paused) video.play().catch(function(){}); }
            }, 1500);
          } catch(e) { AndroidBridge.onLog(String(e)); }
        };
        </script></body></html>
    """.trimIndent()

    private fun String.normalizeStreamUrl(): String = if (startsWith("//")) "https:$this" else this
    private fun String.hostOrNull(): String? =
        runCatching { URL(this).host }.getOrNull()?.takeIf(String::isNotBlank)

    private fun String.normalizeQualityLabel(): String =
        trim().let { if (it.endsWith("p")) it else "${it}p" }

    private fun String.escapeHtml(): String = replace("&", "&amp;").replace("\"", "&quot;")
    private fun String.asDesktopUserAgent(): String = replace(Regex(";?\\s*(wv|Mobile)\\b"), "")

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
        const val BRIDGE_NAME = "AndroidBridge"
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

        // How long a rotation may stay staged before we apply whatever the reload managed to
        // produce. Committing a partial state is the pre-existing behaviour (a short stall while
        // the proxy recovers), so this is a floor on quality, not a new failure mode - but it must
        // stay short enough that the previous token, refreshed SESSION_REFRESH_LEAD_MS (20s) before
        // it expires, is still valid when the forced commit lands.
        const val STAGED_COMMIT_TIMEOUT_MS = 8_000L

        // How many idle WebViews acquireWebView()/releaseWebView() keep warm for reuse. 1 covers
        // the common case (sequential recovery retries / episode switches); extra concurrent
        // callers get their own instance and destroy it on release rather than growing the pool.
        const val MAX_IDLE_WEB_VIEWS = 1

        fun safeFingerprint(value: String?): String {
            if (value.isNullOrBlank()) return "none"
            return MessageDigest.getInstance("SHA-256")
                .digest(value.toByteArray())
                .take(4)
                .joinToString("") { "%02x".format(it.toInt() and 0xff) }
        }
    }

    private class LiveAllohaStreamSession(
        private val handler: Handler,
        override val sourceKey: String,
    ) : AllohaStreamSession {
        override val id: String = UUID.randomUUID().toString()
        private val headers = ConcurrentHashMap<String, String>()
        private val masterUrl = AtomicReference("")
        private val generation = AtomicLong(0L)
        private val expiry = AtomicLong(0L)
        private val view = AtomicReference<WebView?>(null)
        private val stream = AtomicReference<PlayerStreamResolveResult.Stream?>(null)
        private val refreshAction = AtomicReference<(() -> Unit)?>(null)
        private val releaseAction = AtomicReference<((WebView) -> Unit)?>(null)
        private val qualityMasters = ConcurrentHashMap<String, String>()
        private val selectedQuality = AtomicReference<String?>(null)
        private val proxy = AtomicReference<AllohaStreamProxy?>(null)
        private val streamStateLock = Any()

        /**
         * Shadow slot for a session rotation in progress. A WebView reload re-emits its signals in
         * stages - onReady first (carrying the raw bnsi URL the CDN answers with 403), then the
         * correctly signed master and the fresh edge_hash a second or more later. Writing those
         * straight into the live state would leave the proxy serving a half-updated, unauthorized
         * session for that whole window, which is exactly the 2-3s stall Media3 shows as buffering.
         * Instead every update during a rotation lands here and is committed atomically once all
         * three signals arrived, so the still-valid previous token keeps serving until then.
         */
        private class StagedState {
            val headers = mutableMapOf<String, String>()
            var masterUrl: String? = null
            var expiry: Long = 0L
            var stream: PlayerStreamResolveResult.Stream? = null
            var qualities: Map<String, String>? = null
            var hasReady = false
            var hasRefreshedMaster = false
            var hasConfigUpdate = false

            val isComplete: Boolean get() = hasReady && hasRefreshedMaster && hasConfigUpdate
        }

        // Guarded by streamStateLock.
        private var staging: StagedState? = null
        private val stagedCommitTimeout = Runnable {
            synchronized(streamStateLock) {
                if (staging == null) return@Runnable
                Log.w(
                    LOG_TAG,
                    "staged session commit timed out, applying what arrived " +
                            "ready=${staging?.hasReady} master=${staging?.hasRefreshedMaster} " +
                            "config=${staging?.hasConfigUpdate}",
                )
                commitStagedLocked()
            }
        }

        override val initialStream: PlayerStreamResolveResult.Stream
            get() {
                val value = checkNotNull(stream.get())
                val selectedPlaybackUrl = selectedQuality.get()
                    ?.let { checkNotNull(proxy.get()).qualityUrl(it) }
                    ?: playbackUrl
                return value.copy(
                    url = selectedPlaybackUrl,
                    headers = emptyMap(),
                    qualities = qualityUrls,
                    qualityHeaders = emptyMap(),
                )
            }
        val directStream: PlayerStreamResolveResult.Stream
            get() {
                val value = checkNotNull(stream.get())
                val currentHeaders = currentHeaders()
                return value.copy(
                    headers = currentHeaders,
                    qualityHeaders = value.qualities.orEmpty().keys.associateWith { currentHeaders },
                )
            }
        override val playbackUrl: String
            get() = checkNotNull(proxy.get()).playbackUrl
        override val qualityUrls: LinkedHashMap<String, String>
            get() = qualityMasters.keys
                .sortedBy { it.filter(Char::isDigit).toIntOrNull() ?: 0 }
                .associateTo(linkedMapOf()) { it to checkNotNull(proxy.get()).qualityUrl(it) }

        fun attach(webView: WebView, refresh: () -> Unit, release: (WebView) -> Unit) {
            view.set(webView)
            refreshAction.set(refresh)
            releaseAction.set(release)
        }

        fun initialize(value: PlayerStreamResolveResult.Stream) {
            synchronized(streamStateLock) {
                val staged = staging
                if (staged != null) {
                    staged.stream = value
                    staged.masterUrl = value.url
                    staged.qualities = value.qualities
                    staged.headers.clear()
                    staged.headers.putAll(value.headers.mapKeys { it.key.lowercase() })
                    staged.hasReady = true
                    commitStagedIfReadyLocked()
                    return
                }
                stream.set(value)
                masterUrl.set(value.url)
                qualityMasters.clear()
                value.qualities?.let(qualityMasters::putAll)
                selectedQuality.set(
                    value.qualities?.entries?.firstOrNull { it.value == value.url }?.key
                )
                headers.clear()
                headers.putAll(value.headers.mapKeys { it.key.lowercase() })
                generation.incrementAndGet()
            }
        }

        fun startProxy() {
            if (proxy.get() != null) return
            Log.i(LOG_TAG, "starting localhost proxy ${safeHeaderState()}")
            proxy.compareAndSet(
                null,
                AllohaStreamProxy(
                    streamStateProvider = ::currentStreamState,
                    qualityMasterProvider = qualityMasters::get,
                    requestSessionRefresh = ::refresh,
                )
            )
        }

        fun updateHeaders(value: Map<String, String>) {
            synchronized(streamStateLock) {
                val staged = staging
                if (staged != null) {
                    staged.headers.putAll(value.mapKeys { it.key.lowercase() })
                    commitStagedIfReadyLocked()
                } else {
                    headers.putAll(value.mapKeys { it.key.lowercase() })
                }
            }
        }

        fun updateMasterUrl(value: String) {
            synchronized(streamStateLock) {
                if (value.isBlank()) return
                val staged = staging
                if (staged != null) {
                    staged.masterUrl = value
                    staged.hasRefreshedMaster = true
                    commitStagedIfReadyLocked()
                } else {
                    masterUrl.set(value)
                }
            }
        }

        fun updateExpiry(ttlSeconds: Int) = applyExpiry(ttlSeconds, fromConfigUpdate = true)

        fun ensureFallbackExpiry(ttlSeconds: Int) {
            synchronized(streamStateLock) {
                val current = staging?.expiry ?: expiry.get()
                // A guessed TTL must not count as the config_update a staged rotation waits for:
                // only a real one carries the edge_hash the new master is signed with.
                if (current <= System.currentTimeMillis()) {
                    applyExpiry(ttlSeconds, fromConfigUpdate = false)
                }
            }
        }

        private fun applyExpiry(ttlSeconds: Int, fromConfigUpdate: Boolean) {
            synchronized(streamStateLock) {
                val value = System.currentTimeMillis() + ttlSeconds * 1_000L
                val staged = staging
                if (staged != null) {
                    staged.expiry = value
                    if (fromConfigUpdate) {
                        staged.hasConfigUpdate = true
                        commitStagedIfReadyLocked()
                    }
                } else {
                    expiry.set(value)
                }
            }
        }

        private fun commitStagedIfReadyLocked() {
            if (staging?.isComplete == true) commitStagedLocked()
        }

        /** Swaps the staged rotation into the live state in one step. Call under [streamStateLock]. */
        private fun commitStagedLocked() {
            val staged = staging ?: return
            staging = null
            handler.removeCallbacks(stagedCommitTimeout)

            staged.stream?.let(stream::set)
            staged.masterUrl?.takeIf(String::isNotBlank)?.let(masterUrl::set)
            if (staged.expiry > 0L) expiry.set(staged.expiry)
            if (staged.headers.isNotEmpty()) {
                headers.clear()
                headers.putAll(staged.headers)
            }
            staged.qualities?.takeIf { it.isNotEmpty() }?.let { qualities ->
                // Carry the selection across by label: the rotated session serves the same
                // quality ladder behind fresh URLs, so matching on URL would lose the choice.
                val previousLabel = selectedQuality.get()
                qualityMasters.clear()
                qualityMasters.putAll(qualities)
                selectedQuality.set(
                    previousLabel?.takeIf(qualities::containsKey)
                        ?: qualities.entries.firstOrNull { it.value == staged.masterUrl }?.key
                )
            }
            generation.incrementAndGet()
            Log.i(LOG_TAG, "staged session committed ${safeHeaderStateLocked()}")
        }

        override fun currentHeaders(): Map<String, String> = synchronized(streamStateLock) {
            headers.toMap()
        }

        private fun currentStreamState(): AllohaStreamState = synchronized(streamStateLock) {
            AllohaStreamState(
                headers = headers.toMap(),
                masterUrl = masterUrl.get(),
                generation = generation.get(),
                expiresAtMs = expiry.get().takeIf { it > 0L },
            )
        }

        fun safeHeaderState(): String = synchronized(streamStateLock) { safeHeaderStateLocked() }

        private fun safeHeaderStateLocked(): String {
            val host = runCatching { URL(masterUrl.get()).host }.getOrDefault("unknown")
            val ttlSeconds = expiry.get().takeIf { it > 0L }
                ?.let { ((it - System.currentTimeMillis()) / 1_000L).coerceAtLeast(0L) }
            val cookie = sequenceOf(masterUrl.get(), headers["origin"], headers["referer"])
                .filterNotNull()
                .filter(String::isNotBlank)
                .mapNotNull {
                    runCatching { CookieManager.getInstance().getCookie(it) }.getOrNull()
                }
                .firstOrNull(String::isNotBlank)
            return "generation=${generation.get()} host=$host ttl=${ttlSeconds ?: "none"} " +
                    "names=${headers.keys.sorted()} " +
                    "auth=${safeFingerprint(headers["authorizations"])} " +
                    "controls=${safeFingerprint(headers["accepts-controls"])} " +
                    "ua=${safeFingerprint(headers["user-agent"])} " +
                    "cookie=${safeFingerprint(cookie)}"
        }

        override fun currentMasterUrl(): String = synchronized(streamStateLock) { masterUrl.get() }

        /** True while a rotation is staged and has not been committed to the live state yet. */
        val isRotating: Boolean get() = synchronized(streamStateLock) { staging != null }

        override fun expiresAtMs(): Long? = expiry.get().takeIf { it > 0L }
        override fun refresh() {
            // The live headers/master/expiry stay untouched: the current token is still valid for
            // SESSION_REFRESH_LEAD_MS and must keep serving segments while the reloaded WebView
            // assembles the next one. Everything the reload emits lands in `staging` instead and is
            // swapped in atomically by commitStagedLocked().
            synchronized(streamStateLock) {
                if (staging != null) return
                staging = StagedState()
            }
            handler.post {
                handler.removeCallbacks(stagedCommitTimeout)
                handler.postDelayed(stagedCommitTimeout, STAGED_COMMIT_TIMEOUT_MS)
                refreshAction.get()?.invoke()
            }
        }

        override fun selectQuality(label: String) {
            if (qualityMasters.containsKey(label)) selectedQuality.set(label)
        }

        override fun close() {
            handler.removeCallbacks(stagedCommitTimeout)
            synchronized(streamStateLock) { staging = null }
            handler.post {
                proxy.getAndSet(null)?.close()
                refreshAction.set(null)
                val release = releaseAction.getAndSet(null)
                view.getAndSet(null)?.let {
                    it.removeJavascriptInterface(BRIDGE_NAME)
                    it.stopLoading()
                    // Unload the iframe/player so nothing keeps decoding/playing in the background
                    // while this WebView sits idle in the pool, without destroying the instance
                    // itself - see acquireWebView()/releaseWebView().
                    it.loadUrl("about:blank")
                    if (release != null) release(it) else it.destroy()
                }
            }
        }
    }
}
