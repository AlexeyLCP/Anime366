package su.afk.yummy.tv.domain.update.util

fun isVersionNewer(current: String, remote: String): Boolean {
    val cur = current.toVersionParts()
    val rem = remote.toVersionParts()
    val len = maxOf(cur.size, rem.size)
    for (i in 0 until len) {
        val r = rem.getOrElse(i) { 0 }
        val c = cur.getOrElse(i) { 0 }
        if (r > c) return true
        if (r < c) return false
    }
    return false
}

internal fun String.toVersionParts(): List<Int> =
    trim().removePrefix("v").split(Regex("[^0-9]+")).mapNotNull { it.toIntOrNull() }
