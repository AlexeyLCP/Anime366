package su.afk.yummy.tv.feature.details.utils

import su.afk.yummy.tv.core.model.anime.AnimeDetails
import su.afk.yummy.tv.core.model.anime.AnimeVideo
import su.afk.yummy.tv.core.utils.formatting.removeHtmlEntities
import su.afk.yummy.tv.core.utils.formatting.stripHtmlTags
import su.afk.yummy.tv.domain.account.model.VideoSubscription
import su.afk.yummy.tv.feature.details.details.model.SubscriptionOption
import kotlin.time.Duration.Companion.milliseconds

internal val SUBSCRIPTION_REFRESH_DELAY = 350.milliseconds

internal fun List<SubscriptionOption>.subscribedKeys(): Set<String> =
    filter { it.isSubscribed }
        .flatMap { it.subscriptionMatchKeys() }
        .toSet()

internal fun SubscriptionOption.subscriptionMatchKeys(): Set<String> =
    subscriptionMatchKeys(playerId = playerId, player = player, dubbing = dubbing)

private fun VideoSubscription.subscriptionMatchKeys(): Set<String> =
    subscriptionMatchKeys(playerId = playerId, player = player, dubbing = dubbing)

internal fun VideoSubscription.matchesCurrentAnime(
    requestedAnimeId: Int,
    details: AnimeDetails?,
): Boolean {
    if (animeId == requestedAnimeId || animeId == details?.id) return true
    val detailsAnimeUrl = details?.animeUrl.orEmpty()
    return animeUrl.isNotBlank() && detailsAnimeUrl.isNotBlank() && animeUrl == detailsAnimeUrl
}

internal fun VideoSubscription.matchesExactSubscription(video: AnimeVideo): Boolean {
    if (dubbing.isBlank()) return false
    val dubbingMatches =
        dubbing.relaxedSubscriptionPart().matchesRelaxed(video.dubbing.relaxedSubscriptionPart())
    if (!dubbingMatches) return false

    return matchesPlayer(
        playerId = video.playerId,
        player = video.player,
    )
}

internal fun VideoSubscription.matchesPlayer(option: SubscriptionOption): Boolean =
    matchesPlayer(playerId = option.playerId, player = option.player)

private fun VideoSubscription.matchesPlayer(playerId: Int?, player: String): Boolean {
    val playerIdMatches = this.playerId != null && playerId != null && this.playerId == playerId
    if (playerIdMatches) return true

    val playerMatches =
        this.player.relaxedSubscriptionPart().matchesRelaxed(player.relaxedSubscriptionPart())
    if (playerMatches) return true

    return this.playerId == null && this.player.isBlank()
}

internal fun subscriptionMatchKeys(playerId: Int?, player: String, dubbing: String): Set<String> =
    buildSet {
        val normalizedDubbing = dubbing.normalizedSubscriptionPart()
        if (normalizedDubbing.isBlank()) return@buildSet
        if (playerId != null) add("playerId:$playerId|dubbing:$normalizedDubbing")
        val normalizedPlayer = player.normalizedSubscriptionPart()
        if (normalizedPlayer.isNotBlank()) add("player:$normalizedPlayer|dubbing:$normalizedDubbing")
    }

internal fun Set<String>.optimisticSubscriptionState(optimisticStates: Map<String, Boolean>): Boolean? =
    firstNotNullOfOrNull { optimisticStates[it] }

internal fun String.normalizedSubscriptionPart(): String =
    trim().lowercase()

private fun String.relaxedSubscriptionPart(): String =
    trim()
        .lowercase()
        .replace('ё', 'е')
        .stripHtmlTags()
        .removeHtmlEntities()
        .filter { it.isLetterOrDigit() }

private fun String.matchesRelaxed(other: String): Boolean =
    isNotBlank() && other.isNotBlank() && (this == other || this.contains(other) || other.contains(
        this
    ))
