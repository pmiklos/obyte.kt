package app.obyte.client.compose

import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.*

internal class SortedSerializer<T : Any>(serializer: KSerializer<T>) :
    JsonTransformingSerializer<T>(serializer, "sortedPayload") {

    override fun writeTransform(element: JsonElement): JsonElement = element.sorted()

}

internal fun JsonElement.sorted(): JsonElement = when (this) {
    is JsonObject -> json {
        for (key in keys.sorted()) {
            key to this@sorted[key]!!.sorted()
        }
    }
    is JsonArray -> jsonArray {
        this@sorted.forEach {
            +it.sorted()
        }
    }
    else -> this@sorted
}