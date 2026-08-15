package su.afk.yummy.tv.core.storage.schedule

internal class AnimeScheduleStore(private val dao: AnimeScheduleDao) : AnimeScheduleStorage {

    override suspend fun getSchedule(language: String): AnimeScheduleCache? =
        dao.getSchedule(language)

    override suspend fun saveSchedule(cache: AnimeScheduleCache) {
        dao.replaceSchedule(cache)
    }
}
