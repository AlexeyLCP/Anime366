package su.afk.yummy.tv.data.player.extractor.alloha

/** Alloha resolved the session but reports no HLS source for the requested dubbing/episode. */
internal class AllohaSourceUnavailableException(message: String? = null) : Exception(message)
