package su.afk.yummy.tv.domain.account.model

/** Локально сохранённая подписка: озвучка + балансер + id видео, которым она была оформлена. */
data class VideoSubscriptionSelection(
    val animeId: Int,
    val playerKey: String,
    val dubbingKey: String,
    val videoId: Int,
    val updatedAt: Long,
) {
    val subscriptionKey: String get() = "$playerKey|$dubbingKey"
}
