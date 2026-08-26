package su.afk.yummy.tv.domain.update.model

/**
 * Опубликованный релиз приложения: версия без префикса `v`, список изменений
 * и прямая ссылка на APK.
 */
data class AppRelease(
    val version: String,
    val changelog: String,
    val apkUrl: String,
    val updatesCount: Int = 1,
)
