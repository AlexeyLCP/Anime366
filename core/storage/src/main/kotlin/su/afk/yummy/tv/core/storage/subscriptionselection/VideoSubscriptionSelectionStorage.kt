package su.afk.yummy.tv.core.storage.subscriptionselection

/** Абстракция над локальным хранилищем выбранных озвучек — позволяет подменять реализацию в тестах. */
interface VideoSubscriptionSelectionStorage {

    suspend fun getForAnime(userId: Int, animeId: Int): List<VideoSubscriptionSelectionEntry>

    suspend fun save(entry: VideoSubscriptionSelectionEntry)

    suspend fun delete(userId: Int, animeId: Int, playerKey: String, dubbingKey: String)

    suspend fun deleteForPlayer(userId: Int, animeId: Int, playerKey: String)

    suspend fun deleteForUser(userId: Int)
}
