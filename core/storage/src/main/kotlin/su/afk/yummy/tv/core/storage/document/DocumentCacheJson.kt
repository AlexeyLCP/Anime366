package su.afk.yummy.tv.core.storage.document

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.json.Json

/**
 * Формат сериализации кэша документов. Совпадает по настройкам с конфигурацией API, но живёт
 * отдельно: это формат хранения, и он не обязан меняться вместе с сетевым.
 * `ignoreUnknownKeys` здесь особенно важен — записи, сделанные предыдущей версией приложения,
 * должны читаться после добавления полей в DTO.
 */
val DocumentCacheJson: Json = Json {
    ignoreUnknownKeys = true
    explicitNulls = false
}

/**
 * Джсон-обёртка над [DocumentCacheStorage.getOrFetch]: (де)сериализует значение через
 * [DocumentCacheJson], убирая продублированный по репозиториям `decode`/`encode` boilerplate.
 * Тип выводится через `reified`, поэтому вызывающему коду достаточно передать ключ, ttl и загрузчик.
 *
 * Резолв сериализатора для дженерик-параметра идёт рефлексивно (`KClass.serializer()`), поэтому
 * нужен opt-in — сгенерированный сериализатор всё равно используется, warning чисто про
 * API-стабильность.
 */
@OptIn(InternalSerializationApi::class)
suspend inline fun <reified T> DocumentCacheStorage.getOrFetchJson(
    cacheKey: String,
    ttlMs: Long,
    forceRefresh: Boolean = false,
    crossinline fetch: suspend () -> T,
): T = getOrFetch(
    cacheKey = cacheKey,
    ttlMs = ttlMs,
    forceRefresh = forceRefresh,
    decode = { DocumentCacheJson.decodeFromString<T>(it) },
    encode = { DocumentCacheJson.encodeToString(it) },
    fetch = { fetch() },
)
