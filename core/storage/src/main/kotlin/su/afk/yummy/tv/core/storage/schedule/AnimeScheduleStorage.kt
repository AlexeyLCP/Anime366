package su.afk.yummy.tv.core.storage.schedule

/** Абстракция над локальным кэшем расписания выхода серий — позволяет подменять реализацию в тестах. */
interface AnimeScheduleStorage {

    suspend fun getSchedule(language: String): AnimeScheduleCache?

    suspend fun saveSchedule(cache: AnimeScheduleCache)
}
