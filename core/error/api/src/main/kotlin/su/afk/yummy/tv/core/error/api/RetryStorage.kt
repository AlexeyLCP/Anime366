package su.afk.yummy.tv.core.error.api

interface RetryStorage {
    fun put(key: String, action: () -> Unit)

    /** Забрать и удалить */
    fun consume(key: String): (() -> Unit)?

    fun remove(key: String)
}
