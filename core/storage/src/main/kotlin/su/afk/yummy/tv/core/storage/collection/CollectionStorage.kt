package su.afk.yummy.tv.core.storage.collection

/** Абстракция над локальным кэшем подборок/коллекций — позволяет подменять реализацию в тестах. */
interface CollectionStorage {

    suspend fun getCollection(collectionId: Int, language: String): CollectionDetailCache?

    suspend fun saveCollection(cache: CollectionDetailCache)

    suspend fun updateCollectionVote(
        collectionId: Int,
        language: String,
        likes: Int,
        dislikes: Int,
        vote: Int,
    )

    suspend fun deleteCollection(collectionId: Int)

    suspend fun invalidateCatalog()

    suspend fun getCatalogPage(pageKey: String): CollectionCatalogPageCache?

    suspend fun saveCatalogPage(cache: CollectionCatalogPageCache)
}
