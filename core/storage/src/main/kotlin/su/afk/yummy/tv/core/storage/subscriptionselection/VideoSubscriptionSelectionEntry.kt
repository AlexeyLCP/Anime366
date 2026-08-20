package su.afk.yummy.tv.core.storage.subscriptionselection

import androidx.room.Entity

/**
 * Локально запоминает, на какую именно озвучку была оформлена подписка.
 *
 * `GET /users/{id}/lists/subs` возвращает `sub.dubbing` либо пустым, либо перечислением всех озвучек
 * плеера ("AniLibria() AniDUB() ..."), поэтому восстановить озвучку из ответа сервера невозможно —
 * сервер отдаёт только тайтл и плеер. Знание об озвучке есть только в момент нажатия, здесь оно и живёт.
 * `videoId` хранится, чтобы отписываться ровно тем же id, которым подписались: набор серий со временем
 * меняется, и «последняя серия группы» — нестабильный идентификатор.
 */
@Entity(
    tableName = "video_subscription_selection",
    primaryKeys = ["userId", "animeId", "playerKey", "dubbingKey"],
)
data class VideoSubscriptionSelectionEntry(
    val userId: Int,
    val animeId: Int,
    val playerKey: String,
    val dubbingKey: String,
    val videoId: Int,
    val updatedAt: Long = System.currentTimeMillis(),
)
