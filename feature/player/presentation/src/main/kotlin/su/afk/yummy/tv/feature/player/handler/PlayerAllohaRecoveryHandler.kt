package su.afk.yummy.tv.feature.player.handler

import javax.inject.Inject

/** Owns mutable state for the current Alloha fresh-session recovery cycle. */
internal class PlayerAllohaRecoveryHandler @Inject constructor() {
    var isRecovering: Boolean = false
        private set
    var retryCount: Int = 0
        private set
    var selectedQuality: String? = null
    var positionMs: Long = 0L

    fun start(positionMs: Long, selectedQuality: String?) {
        isRecovering = true
        retryCount = 0
        this.selectedQuality = selectedQuality
        this.positionMs = positionMs.coerceAtLeast(0L)
    }

    fun nextAttempt(): Int = ++retryCount

    /**
     * Recovery used to retry without any limit, so a source that cannot come back (the player page
     * silently refusing to load is the common case) left the "cannot resume playback" overlay up
     * forever with no way out. Bounded now, so the UI can fall through to a real error with the
     * change-player / change-dubbing actions.
     */
    fun canRetry(): Boolean = retryCount < MAX_ATTEMPTS

    fun complete(): Int = retryCount.also { reset() }

    fun reset() {
        isRecovering = false
        retryCount = 0
        selectedQuality = null
        positionMs = 0L
    }

    companion object {
        const val MAX_ATTEMPTS = 4
    }
}
