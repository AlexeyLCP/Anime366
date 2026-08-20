package su.afk.yummy.tv.core.tv.api

/**
 * Порт, через который `core:tv` получает контент для системных поверхностей Android TV,
 * не зная о фичах. Реализация живёт в фиче, владеющей источником данных.
 */
interface TvChannelContentProvider {

    /** Свежие релизы для preview-канала. Пустой список — канал не обновляется. */
    suspend fun newReleases(): List<TvChannelContent>

    /** Обновляет источник контента из сети (периодическая фоновая синхронизация). */
    suspend fun refresh()
}
