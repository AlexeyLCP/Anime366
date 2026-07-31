package su.afk.yummy.tv.data.messages.dto

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.intOrNull

/**
 * Терпимо читает `Boolean`, приходящий по-разному: как настоящий булев (`true`/`false`) либо как
 * число `0`/`1`. В общем (глобальном) чате yani поля `deleted`/`edited` приходят числами, а не
 * булевыми, из-за чего стандартный декодер падает. Личные диалоги при этом отдают булевы —
 * оба варианта обрабатываются корректно.
 */
object FlexibleBooleanSerializer : KSerializer<Boolean> {
    override val descriptor =
        PrimitiveSerialDescriptor("FlexibleBoolean", PrimitiveKind.BOOLEAN)

    override fun deserialize(decoder: Decoder): Boolean {
        val jsonDecoder = decoder as? JsonDecoder ?: return decoder.decodeBoolean()
        val primitive = jsonDecoder.decodeJsonElement() as? JsonPrimitive ?: return false
        primitive.booleanOrNull?.let { return it }
        primitive.intOrNull?.let { return it != 0 }
        return primitive.content.equals("true", ignoreCase = true)
    }

    override fun serialize(encoder: Encoder, value: Boolean) = encoder.encodeBoolean(value)
}
