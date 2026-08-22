package su.afk.yummy.tv.core.network.yani

import kotlinx.serialization.json.Json

/** Единая конфигурация Json для всех запросов к API yani.tv. */
val YaniApiJson: Json = Json {
    ignoreUnknownKeys = true
    explicitNulls = false
    // yani регулярно шлёт null там, где по схеме строка или число (например sub.dubbing в
    // /users/{id}/lists/subs). Для non-null поля с дефолтом explicitNulls этого не покрывает —
    // без коэрции такой ответ роняет разбор целиком. Поля без дефолта (тела запросов) флаг
    // не затрагивает, они по-прежнему падают громко.
    coerceInputValues = true
}
