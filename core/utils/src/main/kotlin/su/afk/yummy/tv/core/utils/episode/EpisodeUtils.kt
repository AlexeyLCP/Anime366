package su.afk.yummy.tv.core.utils.episode

fun String.episodeNumberOrNull(): Double? {
    val normalized = trim().replace(',', '.')
    return normalized.toDoubleOrNull()
        ?: EPISODE_NUMBER_REGEX
            .find(normalized)
            ?.value
            ?.replace(',', '.')
            ?.toDoubleOrNull()
}

fun String.isPlaceholderEpisode(): Boolean = trim().isEmpty() || trim() == "-"

/**
 * Нормализованный ключ серии для группировки/дедупа.
 * Разные озвучки присылают номер по-разному (`"01"` vs `"1"`), схлопываем их в один ключ.
 */
fun String.episodeGroupKey(): String =
    trim().trimStart('0').ifEmpty { trim() }.lowercase()

private val EPISODE_NUMBER_REGEX = Regex("""\d+(?:[.,]\d+)?""")
