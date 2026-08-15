package su.afk.yummy.tv.core.storage.search

/** Абстракция над локальным кэшем поиска, фильтров и жанров — позволяет подменять реализацию в тестах. */
interface SearchStorage {

    suspend fun getPage(pageKey: String): SearchPageCache?

    suspend fun savePage(
        cache: SearchPageCache,
        prunePagesCachedBefore: Long? = null,
    )

    suspend fun getFilterOptions(language: String): SearchFilterOptionsCache?

    suspend fun saveFilterOptions(cache: SearchFilterOptionsCache)
}
