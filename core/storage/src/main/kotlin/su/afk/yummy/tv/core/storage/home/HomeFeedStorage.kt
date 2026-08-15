package su.afk.yummy.tv.core.storage.home

/** Абстракция над локальным кэшем главной ленты — позволяет подменять реализацию в тестах. */
interface HomeFeedStorage {

    suspend fun getFeed(
        language: String,
        watchSignature: String = HOME_FEED_GENERIC_WATCH_SIGNATURE,
    ): HomeFeedCache?

    suspend fun saveFeed(cache: HomeFeedCache)

    suspend fun deleteContinueWatchingByAnimeId(animeId: Int)
}
