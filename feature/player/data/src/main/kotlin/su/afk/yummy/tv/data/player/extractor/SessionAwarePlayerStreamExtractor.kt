package su.afk.yummy.tv.data.player.extractor

import android.content.Context
import su.afk.yummy.tv.domain.player.model.AllohaStreamSession
import su.afk.yummy.tv.domain.player.model.PlayerStreamRequest

/**
 * A [PlayerStreamExtractor] that can also hand out a long-lived, rotating playback session
 * (currently only Alloha needs this) instead of a one-shot resolved stream. Lets callers depend
 * on this capability by interface rather than on a concrete extractor class.
 */
internal interface SessionAwarePlayerStreamExtractor : PlayerStreamExtractor {
    suspend fun openSession(request: PlayerStreamRequest, context: Context): AllohaStreamSession?
}
