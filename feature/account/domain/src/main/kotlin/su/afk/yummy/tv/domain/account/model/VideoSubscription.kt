package su.afk.yummy.tv.domain.account.model

/**
 * Строка списка `GET /users/{id}/lists/subs` — одна подписка на тайтл.
 *
 * [dubbing] НЕ содержит озвучку подписки: сервер возвращает либо пустую строку, либо перечисление всех
 * озвучек плеера («AniLibria() AniDUB() ...»), одинаковое для всех подписок этого балансера. Определить
 * по нему выбранную озвучку нельзя — для этого есть
 * [su.afk.yummy.tv.domain.account.model.VideoSubscriptionSelection].
 */
data class VideoSubscription(
    val animeId: Int,
    val animeUrl: String,
    val playerId: Int?,
    val player: String,
    val dubbing: String,
    val posterUrl: String?,
    val title: String,
)
