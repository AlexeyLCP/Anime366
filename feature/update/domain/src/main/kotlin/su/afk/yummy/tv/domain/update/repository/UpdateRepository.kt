package su.afk.yummy.tv.domain.update.repository

import su.afk.yummy.tv.domain.update.model.AppRelease

interface UpdateRepository {

    /** Последний опубликованный релиз или null, если релизов нет либо у него нет APK. */
    suspend fun latestRelease(): AppRelease?
}
