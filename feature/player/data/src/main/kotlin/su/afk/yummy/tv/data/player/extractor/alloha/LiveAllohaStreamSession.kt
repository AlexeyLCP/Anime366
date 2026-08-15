package su.afk.yummy.tv.data.player.extractor.alloha

import android.os.Handler
import android.util.Log
import android.webkit.CookieManager
import android.webkit.WebView
import su.afk.yummy.tv.domain.player.model.AllohaStreamSession
import su.afk.yummy.tv.domain.player.model.PlayerStreamResolveResult
import java.net.URL
import java.security.MessageDigest
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.atomic.AtomicReference

private const val LOG_TAG = "AllohaExtractor"

// How long a rotation may stay staged before we apply whatever the reload managed to produce.
// Committing a partial state is the pre-existing behaviour (a short stall while the proxy
// recovers), so this is a floor on quality, not a new failure mode - but it must stay short
// enough that the previous token, refreshed SESSION_REFRESH_LEAD_MS (20s) before it expires, is
// still valid when the forced commit lands.
private const val STAGED_COMMIT_TIMEOUT_MS = 8_000L

internal class LiveAllohaStreamSession(
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
                // itself - see AllohaWebViewPool.
                it.loadUrl("about:blank")
                if (release != null) release(it) else it.destroy()
            }
        }
    }
}

private fun safeFingerprint(value: String?): String {
    if (value.isNullOrBlank()) return "none"
    return MessageDigest.getInstance("SHA-256")
        .digest(value.toByteArray())
        .take(4)
        .joinToString("") { "%02x".format(it.toInt() and 0xff) }
}
