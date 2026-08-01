package su.afk.yummy.tv.domain.player.model

data class PlayerStreamRequest(
    val iframeUrl: String,
    val autoQualityLabel: String,
    val sessionFallbackTtlSeconds: Int? = null,
    val reusePlaybackSession: Boolean = true,
    /** Пропускает и перезаписывает закэшированный resolve — предыдущий результат известно плохой. */
    val forceRefresh: Boolean = false,
)
