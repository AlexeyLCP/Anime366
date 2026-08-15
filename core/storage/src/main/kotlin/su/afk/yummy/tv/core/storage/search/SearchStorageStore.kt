package su.afk.yummy.tv.core.storage.search

internal class SearchStorageStore(private val dao: SearchStorageDao) : SearchStorage {

    override suspend fun getPage(pageKey: String): SearchPageCache? =
        dao.getPage(pageKey)

    override suspend fun savePage(
        cache: SearchPageCache,
        prunePagesCachedBefore: Long?,
    ) {
        dao.replacePage(cache, prunePagesCachedBefore)
    }

    override suspend fun getFilterOptions(language: String): SearchFilterOptionsCache? =
        dao.getFilterOptions(language)

    override suspend fun saveFilterOptions(cache: SearchFilterOptionsCache) {
        dao.replaceFilterOptions(cache)
    }
}
