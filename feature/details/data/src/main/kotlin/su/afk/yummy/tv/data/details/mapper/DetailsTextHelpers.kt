package su.afk.yummy.tv.data.details.mapper

/**
 * Возвращает осмысленный текст или null: пустые строки и API-заглушки
 * "unknown"/"unknow" (в любом регистре) трактуются как отсутствие значения.
 */
internal fun String?.knownText(): String? {
    val value = this?.trim().orEmpty()
    return value.takeIf {
        it.isNotBlank() &&
                !it.equals("unknown", ignoreCase = true) &&
                !it.equals("unknow", ignoreCase = true)
    }
}
