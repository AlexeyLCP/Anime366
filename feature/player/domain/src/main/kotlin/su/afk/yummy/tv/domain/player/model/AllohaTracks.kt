package su.afk.yummy.tv.domain.player.model

/**
 * One dubbing/voice-over Alloha offers for the current episode, taken from the `hlsSource` array of
 * its own `bnsi` response - the same list its player shows behind the gear icon. Each entry carries
 * its own full quality ladder, so switching is a stream swap inside the live session (like quality),
 * not a new extraction.
 */
data class AllohaAudioTrack(
    val id: String,
    val label: String,
    val isDefault: Boolean = false,
)

/** A subtitle file (`tracks` entry of the `bnsi` response), served as an external side-loaded track. */
data class AllohaSubtitleTrack(
    val label: String,
    val url: String,
    val language: String?,
    /** Lowercase file extension of the original source (`vtt`, `srt`, ...); [url] may be proxied. */
    val format: String? = null,
)
