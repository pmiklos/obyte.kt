package app.obyte.client.compose

import app.obyte.client.protocol.PaymentPayload
import app.obyte.client.protocol.obyteJson
import app.obyte.client.util.encodeBase64
import app.obyte.client.util.sha256
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.*

fun PaymentPayload.hash(): String =
    obyteJson.stringify(SortedSerializer(PaymentPayload.serializer()), this).sha256().encodeBase64()

class SortedSerializer<T : Any>(serializer: KSerializer<T>) :
    JsonTransformingSerializer<T>(serializer, "sortedPayload") {

    override fun writeTransform(element: JsonElement): JsonElement = when (element) {
        is JsonObject -> json {
            for (key in element.keys.sorted()) {
                key to writeTransform(element[key]!!)
            }
        }
        is JsonArray -> jsonArray { element.forEach { +writeTransform(it) } }
        else -> element
    }

}
