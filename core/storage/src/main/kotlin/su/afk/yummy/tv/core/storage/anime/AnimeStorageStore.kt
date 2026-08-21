package su.afk.yummy.tv.core.storage.anime

internal class AnimeStorageStore(private val dao: AnimeStorageDao) : AnimeStorage {

    override suspend fun getDetails(animeId: Int, language: String): AnimeDetailsCache? =
        dao.getDetails(animeId, language)

    override suspend fun saveDetails(cache: AnimeDetailsCache) {
        dao.replaceDetails(cache)
    }

    override suspend fun deleteDetails(animeId: Int, language: String) {
        dao.deleteDetails(animeId, language)
    }

    override suspend fun expireAllVideos() {
        dao.expireAllVideos()
    }

    override suspend fun expireAllDetails() {
        dao.expireAllDetails()
    }

    override suspend fun getVideos(animeId: Int, language: String): AnimeVideosCache? =
        dao.getVideos(animeId, language)

    override suspend fun saveVideos(cache: AnimeVideosCache) {
        dao.replaceVideos(cache)
    }

    override suspend fun getRecommendations(
        animeId: Int,
        language: String,
        fromAi: Boolean,
    ): AnimeRecommendationsCache? =
        dao.getRecommendations(animeId, language, fromAi)

    override suspend fun saveRecommendations(cache: AnimeRecommendationsCache) {
        dao.replaceRecommendations(cache)
    }

    override suspend fun getTrailers(animeId: Int, language: String): AnimeTrailersCache? =
        dao.getTrailers(animeId, language)

    override suspend fun saveTrailers(cache: AnimeTrailersCache) {
        dao.replaceTrailers(cache)
    }
}
