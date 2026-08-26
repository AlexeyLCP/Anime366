package su.afk.yummy.tv.domain.videodownload.model

/**
 * Как ключуются ресурсы загрузки в кэше. Схема фиксируется в момент постановки в очередь и
 * персистится: и загрузка, и воспроизведение, и удаление обязаны читать один и тот же вариант.
 *
 * [Legacy] — всё, что скачано до перехода на неймспейсинг: HLS/DASH-сегменты лежат под сырыми URL,
 * потому что `HlsDownloader` не применяет `MediaItem.customCacheKey`. Такие данные невозможно
 * атрибутировать конкретной загрузке, поэтому их нельзя ни точечно удалить, ни безопасно вычистить.
 */
enum class VideoDownloadCacheKeyScheme(val storageValue: Int) {
    Legacy(0),
    Namespaced(1),
    NamespacedRotating(2);

    companion object {
        fun fromStorageValue(value: Int): VideoDownloadCacheKeyScheme =
            entries.firstOrNull { it.storageValue == value } ?: Legacy
    }
}
