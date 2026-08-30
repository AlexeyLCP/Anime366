package su.afk.yummy.tv.core.network.yani

const val ANIME365_DEFAULT_HOST = "anime-365.ru"
const val YANI_BASE_URL = "https://$ANIME365_DEFAULT_HOST/api"
const val ANIME365_SITE_URL = "https://$ANIME365_DEFAULT_HOST"
const val ANIME365_USER_AGENT = "Anime366"

val ANIME365_HOSTS = setOf(
    ANIME365_DEFAULT_HOST,
    "smotret-anime.org",
    "smotret-anime.app",
    "smotret-anime.net",
    "smotret-anime.com",
)

fun normalizeAnime365Host(raw: String): String {
    val host = raw.trim()
        .removePrefix("https://")
        .removePrefix("http://")
        .substringBefore('/')
        .substringBefore(':')
        .lowercase()
    return if (host in ANIME365_HOSTS) host else ANIME365_DEFAULT_HOST
}
