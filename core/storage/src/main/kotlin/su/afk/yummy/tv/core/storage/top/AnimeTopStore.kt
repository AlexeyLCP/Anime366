package su.afk.yummy.tv.core.storage.top

internal class AnimeTopStore(private val dao: AnimeTopDao) : AnimeTopStorage {

    override suspend fun getPage(
        type: String,
        language: String,
        limit: Int,
        offset: Int,
    ): AnimeTopPageCache? =
        dao.getPage(type, language, limit, offset)

    override suspend fun savePage(
        cache: AnimeTopPageCache,
        prunePagesCachedBefore: Long?,
    ) {
        dao.replacePage(cache, prunePagesCachedBefore)
    }
}
