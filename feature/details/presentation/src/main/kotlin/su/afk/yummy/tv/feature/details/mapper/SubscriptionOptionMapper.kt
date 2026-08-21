package su.afk.yummy.tv.feature.details.mapper

import su.afk.yummy.tv.core.model.anime.AnimeVideo
import su.afk.yummy.tv.domain.account.model.SubscriptionKeys
import su.afk.yummy.tv.feature.details.details.model.SubscriptionOption

/**
 * Собирает список «озвучка + балансер» из плоского списка видео тайтла.
 *
 * Одна запись `/anime/{id}/videos` — это серия × озвучка × балансер, поэтому подписка живёт на уровне
 * группы. Состояние берётся из поля `subscribed` того же ответа: авторизованному пользователю сервер
 * проставляет его каждому видео. Списку подписок `/users/{id}/lists/subs` здесь делать нечего — он не
 * сообщает, к какой озвучке подписка относится.
 */
internal fun List<AnimeVideo>.toSubscriptionOptions(
    pendingStates: Map<String, Boolean> = emptyMap(),
): List<SubscriptionOption> =
    filter { it.id > 0 && it.dubbing.isNotBlank() }
        .groupBy { SubscriptionKeys.subscriptionKey(it.playerId, it.player, it.dubbing) }
        .map { (key, videos) ->
            val latest = videos.maxWith(
                compareBy<AnimeVideo> { it.episode.toIntOrNull() ?: 0 }.thenBy { it.id }
            )
            SubscriptionOptionWithViews(
                option = SubscriptionOption(
                    key = key,
                    playerId = latest.playerId,
                    player = latest.player,
                    dubbing = latest.dubbing,
                    episodesCount = videos.size,
                    // Отписываться безопаснее тем видео, которое сервер сам считает подписанным.
                    subscriptionVideoId = videos.firstOrNull { it.isSubscribed }?.id ?: latest.id,
                    isSubscribed = pendingStates[key] ?: videos.any { it.isSubscribed },
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
