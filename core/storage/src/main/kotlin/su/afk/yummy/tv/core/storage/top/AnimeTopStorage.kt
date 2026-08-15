package su.afk.yummy.tv.core.storage.top

/** Абстракция над локальным кэшем топов тайтлов — позволяет подменять реализацию в тестах. */
interface AnimeTopStorage {

    suspend fun getPage(
        type: String,
        language: String,
        limit: Int,
        offset: Int,
    ): AnimeTopPageCache?

    suspend fun savePage(
        cache: AnimeTopPageCache,
        prunePagesCachedBefore: Long? = null,
    )
}
