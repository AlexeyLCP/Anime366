package su.afk.yummy.tv.core.utils.network

fun Map<String, String>.safeHttpHeaderNames(): List<String> =
    keys.map { it.lowercase() }
        .filterNot { it == "cookie" || it == "authorization" }
        .sorted()
