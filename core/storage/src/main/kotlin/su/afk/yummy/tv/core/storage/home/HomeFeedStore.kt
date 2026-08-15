package su.afk.yummy.tv.core.storage.home

internal class HomeFeedStore(private val dao: HomeFeedDao) : HomeFeedStorage {

    override suspend fun getFeed(
        language: String,
        watchSignature: String,
    ): HomeFeedCache? =
        dao.getFeed(language, watchSignature)

    override suspend fun saveFeed(cache: HomeFeedCache) {
        dao.replaceFeed(cache)
    }

    override suspend fun deleteContinueWatchingByAnimeId(animeId: Int) {
        dao.deleteItemsByContainerAndItemId(
            container = HOME_FEED_CONTAINER_CONTINUE_WATCHING,
            itemId = animeId,
        )
    }
}
