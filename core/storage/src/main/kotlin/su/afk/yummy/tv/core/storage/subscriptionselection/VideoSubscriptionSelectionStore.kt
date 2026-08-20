package su.afk.yummy.tv.core.storage.subscriptionselection

internal class VideoSubscriptionSelectionStore(
    private val dao: VideoSubscriptionSelectionDao,
) : VideoSubscriptionSelectionStorage {

    override suspend fun getForAnime(
        userId: Int,
        animeId: Int,
    ): List<VideoSubscriptionSelectionEntry> = dao.getForAnime(userId, animeId)

    override suspend fun save(entry: VideoSubscriptionSelectionEntry) = dao.save(entry)

    override suspend fun delete(
        userId: Int,
        animeId: Int,
        playerKey: String,
        dubbingKey: String,
    ) = dao.delete(userId, animeId, playerKey, dubbingKey)

    override suspend fun deleteForPlayer(userId: Int, animeId: Int, playerKey: String) =
        dao.deleteForPlayer(userId, animeId, playerKey)

    override suspend fun deleteForUser(userId: Int) = dao.deleteForUser(userId)
}
