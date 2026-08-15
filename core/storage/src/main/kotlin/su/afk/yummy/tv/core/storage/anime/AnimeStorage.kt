package su.afk.yummy.tv.core.storage.anime

/** Абстракция над локальным кэшем деталей тайтла — позволяет подменять реализацию в тестах. */
interface AnimeStorage {

    suspend fun getDetails(animeId: Int, language: String): AnimeDetailsCache?

    suspend fun saveDetails(cache: AnimeDetailsCache)

    suspend fun deleteDetails(animeId: Int, language: String)

    suspend fun expireAllDetails()

    suspend fun getVideos(animeId: Int, language: String): AnimeVideosCache?

    suspend fun saveVideos(cache: AnimeVideosCache)

    suspend fun getRecommendations(
        animeId: Int,
        language: String,
        fromAi: Boolean,
    ): AnimeRecommendationsCache?

    suspend fun saveRecommendations(cache: AnimeRecommendationsCache)

    suspend fun getTrailers(animeId: Int, language: String): AnimeTrailersCache?

    suspend fun saveTrailers(cache: AnimeTrailersCache)
}
