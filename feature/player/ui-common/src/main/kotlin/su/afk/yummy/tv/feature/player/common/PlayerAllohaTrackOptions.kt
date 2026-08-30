package su.afk.yummy.tv.feature.player.common

import androidx.media3.common.MimeTypes
import su.afk.yummy.tv.domain.player.model.AllohaAudioTrack
import su.afk.yummy.tv.domain.player.model.AllohaSubtitleTrack

/**
 * Alloha's own per-episode dubbing and subtitle lists (what its player shows behind the gear icon),
 * shaped for the same pickers [PlayerTrackSelectionState] drives for native in-stream tracks.
 * Alloha is the only source that reports these, so for everything else these lists stay empty and
 * the native Media3 path applies instead.
 */
class PlayerAllohaTracks(
    val audioTracks: List<AllohaAudioTrack>,
    private val selectedAudioId: String?,
    val subtitles: List<AllohaSubtitleTrack>,
    private val selectedSubtitleIndex: Int?,
    private val subtitlesOffLabel: String,
) {
    val isAvailable: Boolean get() = audioTracks.isNotEmpty() || subtitles.isNotEmpty()
    val hasAudioChoice: Boolean get() = audioTracks.isNotEmpty()
    val hasSubtitleChoice: Boolean get() = subtitles.isNotEmpty()

    val audioOptions: List<PlayerTrackOption>
        get() = audioTracks.mapIndexed { index, track -> PlayerTrackOption(index, track.label) }

    /** "Off" is always first, mirroring the native subtitle picker. */
    val subtitleOptions: List<PlayerTrackOption>
        get() = buildList {
            add(PlayerTrackOption(PLAYER_TEXT_TRACK_OFF_INDEX, subtitlesOffLabel))
            subtitles.forEachIndexed { index, track ->
                add(PlayerTrackOption(index + 1, track.label))
            }
        }

    val selectedAudioIndex: Int
        get() = audioTracks.indexOfFirst { it.id == selectedAudioId }
            .takeIf { it >= 0 }
            ?: audioTracks.indexOfFirst(AllohaAudioTrack::isDefault).coerceAtLeast(0)

    val selectedSubtitleOptionIndex: Int
        get() = selectedSubtitleIndex
            ?.takeIf { it in subtitles.indices }
            ?.plus(1)
            ?: PLAYER_TEXT_TRACK_OFF_INDEX

    fun audioIdAt(optionIndex: Int): String? = audioTracks.getOrNull(optionIndex)?.id

    /** Maps a subtitle option index back to a list index, or null for "off". */
    fun subtitleIndexAt(optionIndex: Int): Int? =
        (optionIndex - 1).takeIf { it in subtitles.indices }
}

/**
 * Media3 mime type for a side-loaded Alloha subtitle. Derived from the original file extension
 * because the loopback proxy URL the player actually loads carries no extension of its own.
 */
fun AllohaSubtitleTrack.mediaMimeType(): String = when (format) {
    "srt" -> MimeTypes.APPLICATION_SUBRIP
    "ass", "ssa" -> MimeTypes.TEXT_SSA
    "ttml", "xml", "dfxp" -> MimeTypes.APPLICATION_TTML
    else -> MimeTypes.TEXT_VTT
}
