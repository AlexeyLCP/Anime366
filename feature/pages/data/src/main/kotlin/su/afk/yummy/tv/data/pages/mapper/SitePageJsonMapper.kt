package su.afk.yummy.tv.data.pages.mapper

import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull

internal fun JsonElement.findString(keys: Set<String>): String? = when (this) {
    is JsonObject -> {
        entries.firstNotNullOfOrNull { (key, value) ->
            value.takeIf { key.lowercase() in keys }?.let { (it as? JsonPrimitive)?.contentOrNull }
        } ?: values.filterNot { it is JsonPrimitive }
            .firstNotNullOfOrNull { value -> value.findString(keys) }
    }

    is JsonArray -> firstNotNullOfOrNull { value -> value.findString(keys) }
    else -> null
}
