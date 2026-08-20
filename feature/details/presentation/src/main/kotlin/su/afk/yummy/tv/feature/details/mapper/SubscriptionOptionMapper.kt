package su.afk.yummy.tv.feature.details.mapper

import su.afk.yummy.tv.core.model.anime.AnimeVideo
import su.afk.yummy.tv.domain.account.model.AnimeSubscriptionState
import su.afk.yummy.tv.domain.account.model.SubscriptionKeys
import su.afk.yummy.tv.feature.details.details.model.SubscriptionOption

/**
 * Собирает список «озвучка + балансер» из плоского списка видео тайтла.
 *
 * Одна запись `/anime/{id}/videos` — это серия × озвучка × балансер, поэтому подписка живёт на уровне
 * группы. Состояние подписки берётся из [AnimeSubscriptionState]: сервер в списке подписок озвучку не
 * возвращает, сопоставлять по названию нечего.
 */
internal fun List<AnimeVideo>.toSubscriptionOptions(
    state: AnimeSubscriptionState = AnimeSubscriptionState(),
    pendingStates: Map<String, Boolean> = emptyMap(),
): List<SubscriptionOption> =
    filter { it.id > 0 && it.dubbing.isNotBlank() }
        .groupBy { SubscriptionKeys.subscriptionKey(it.playerId, it.player, it.dubbing) }
        .mapNotNull { (key, videos) ->
            val representative = videos.maxWithOrNull(
                compareBy<AnimeVideo> { it.episode.toIntOrNull() ?: 0 }.thenBy { it.id }
            ) ?: return@mapNotNull null
            SubscriptionOptionWithViews(
                option = SubscriptionOption(
                    key = key,
                    playerId = representative.playerId,
                    player = representative.player,
                    dubbing = representative.dubbing,
                    episodesCount = videos.size,
                    subscriptionVideoId = state.videoIdsByKey[key] ?: representative.id,
                    isSubscribed = pendingStates[key] ?: (key in state.subscribedKeys),
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
        .map { it.option }

private data class SubscriptionOptionWithViews(
    val option: SubscriptionOption,
    val totalViews: Int,
)
