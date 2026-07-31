package su.afk.yummy.tv.core.network

import kotlinx.serialization.InternalSerializationApi
import su.afk.yummy.tv.core.storage.document.DocumentCacheStore

/**
 * Джсон-обёртка над [DocumentCacheStore.getOrFetch]: (де)сериализует значение через [YaniApiJson],
 * убирая продублированный по репозиториям `decode`/`encode` boilerplate. Тип выводится через
 * `reified`, поэтому вызывающему коду достаточно передать ключ, ttl и загрузчик.
 *
 * Резолв сериализатора для дженерик-параметра идёт рефлексивно (`KClass.serializer()`), поэтому
 * нужен opt-in — сгенерированный сериализатор всё равно используется, warning чисто про API-стабильность.
 */
@OptIn(InternalSerializationApi::class)
suspend inline fun <reified T> DocumentCacheStore.getOrFetchJson(
    cacheKey: String,
    ttlMs: Long,
    forceRefresh: Boolean = false,
    crossinline fetch: suspend () -> T,
): T = getOrFetch(
    cacheKey = cacheKey,
    ttlMs = ttlMs,
    forceRefresh = forceRefresh,
    decode = { YaniApiJson.decodeFromString<T>(it) },
    encode = { YaniApiJson.encodeToString(it) },
    fetch = { fetch() },
)
