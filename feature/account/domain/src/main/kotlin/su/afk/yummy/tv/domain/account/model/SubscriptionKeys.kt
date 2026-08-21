package su.afk.yummy.tv.domain.account.model

/**
 * Ключи подписки на озвучку конкретного балансера.
 *
 * Плеер сравнивается по `player_id` — он стабилен (2 Alloha, 3 CVH, 4 Kodik, 7 Sibnet, 8 VK) и приходит
 * и в `/anime/{id}/videos`, и в `/users/{id}/lists/subs`. Имя плеера — запасной вариант: в списке видео
 * оно приходит как «Плеер Kodik», а в подписках как «Kodik», поэтому префикс снимается.
 * У озвучки идентификатора в API нет вообще, есть только название.
 */
object SubscriptionKeys {

    private val PLAYER_PREFIXES = listOf("плеер ", "player ")

    fun playerKey(playerId: Int?, player: String): String =
        if (playerId != null && playerId > 0) {
            "id:$playerId"
        } else {
            "name:${normalize(stripPlayerPrefix(player))}"
        }

    fun dubbingKey(dubbing: String): String = normalize(dubbing)

    /**
     * Ключ строки списка «Мои подписки» — пара «тайтл + балансер».
     *
     * `/users/{id}/lists/subs` отдаёт по строке на подписку, поэтому две озвучки одного балансера
     * дают две неотличимые записи (озвучку список не сообщает, см. docs/subscriptions.md).
     */
    fun animePlayerKey(animeId: Int, playerId: Int?, player: String): String =
        "$animeId|${playerKey(playerId, player)}"

    fun subscriptionKey(playerId: Int?, player: String, dubbing: String): String =
        "${playerKey(playerId, player)}|${dubbingKey(dubbing)}"

    private fun stripPlayerPrefix(player: String): String {
        val trimmed = player.trim()
        val prefix = PLAYER_PREFIXES.firstOrNull { trimmed.startsWith(it, ignoreCase = true) }
        return if (prefix == null) trimmed else trimmed.substring(prefix.length)
    }

    private fun normalize(value: String): String =
        value.trim().lowercase().replace('ё', 'е').filter { it.isLetterOrDigit() }
}
