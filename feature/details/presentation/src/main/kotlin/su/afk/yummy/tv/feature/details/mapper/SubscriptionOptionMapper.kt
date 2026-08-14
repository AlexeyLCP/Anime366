package su.afk.yummy.tv.feature.details.mapper

import su.afk.yummy.tv.core.model.anime.AnimeVideo
import su.afk.yummy.tv.domain.account.model.VideoSubscription
import su.afk.yummy.tv.feature.details.details.model.SubscriptionOption
import su.afk.yummy.tv.feature.details.utils.matchesExactSubscription
import su.afk.yummy.tv.feature.details.utils.matchesPlayer
import su.afk.yummy.tv.feature.details.utils.normalizedSubscriptionPart
import su.afk.yummy.tv.feature.details.utils.optimisticSubscriptionState
import su.afk.yummy.tv.feature.details.utils.subscriptionMatchKeys

internal fun List<AnimeVideo>.toSubscriptionOptions(
    remoteSubscriptions: List<VideoSubscription> = emptyList(),
    optimisticKeys: Set<String> = emptySet(),
    optimisticStates: Map<String, Boolean> = emptyMap(),
): List<SubscriptionOption> {
    val sortedOptions = filter { it.id > 0 && it.dubbing.isNotBlank() }
        .groupBy { subscriptionGroupKey(it.playerId, it.player, it.dubbing) }
        .values
        .mapNotNull { videos ->
            val representative = videos.maxWithOrNull(
                compareBy<AnimeVideo> { it.episode.toIntOrNull() ?: 0 }
                    .thenBy { it.id }
            ) ?: return@mapNotNull null
            val matchKeys = subscriptionMatchKeys(
                playerId = representative.playerId,
                player = representative.player,
                dubbing = representative.dubbing,
            )
            val optimisticState = matchKeys.optimisticSubscriptionState(optimisticStates)
            SubscriptionOptionWithViews(
                option = SubscriptionOption(
                    key = subscriptionGroupKey(
                        representative.playerId,
                        representative.player,
                        representative.dubbing
                    ),
                    playerId = representative.playerId,
                    player = representative.player,
                    dubbing = representative.dubbing,
                    episodesCount = videos.size,
                    representativeVideoId = representative.id,
                    isSubscribed = optimisticState
                        ?: (matchKeys.any { it in optimisticKeys } ||
                                remoteSubscriptions.any { it.matchesExactSubscription(representative) }),
                ),
                totalViews = videos.sumOf { it.views ?: 0 },
            )
        }
        .groupBy { it.option.dubbing.trim().lowercase() }
        .values
        .sortedByDescending { group -> group.sumOf { it.totalViews } }
        .flatMap { group ->
            group.sortedWith(
                compareByDescending<SubscriptionOptionWithViews> { it.totalViews }
                    .thenBy { it.option.player }
            )
        }
    val blankDubbingFallbackKeys = remoteSubscriptions
        .filter { it.dubbing.isBlank() }
        .mapNotNull { subscription ->
            sortedOptions.firstOrNull { subscription.matchesPlayer(it.option) }?.option?.key
        }
        .toSet()

    return sortedOptions.map { item ->
        val option = item.option
        val optimisticState =
            option.subscriptionMatchKeys().optimisticSubscriptionState(optimisticStates)
        if (option.key in blankDubbingFallbackKeys && optimisticState != false) {
            option.copy(isSubscribed = true)
        } else {
            option
        }
    }
}

private data class SubscriptionOptionWithViews(
    val option: SubscriptionOption,
    val totalViews: Int,
)

private fun subscriptionGroupKey(playerId: Int?, player: String, dubbing: String): String {
    val normalizedDubbing = dubbing.normalizedSubscriptionPart()
    val normalizedPlayer = player.normalizedSubscriptionPart()
    return "playerId:${playerId ?: -1}|player:$normalizedPlayer|dubbing:$normalizedDubbing"
}
