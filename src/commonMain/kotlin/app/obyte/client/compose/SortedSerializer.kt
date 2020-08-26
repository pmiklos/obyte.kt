package app.obyte.client.compose

import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.*

internal class SortedSerializer<T : Any>(serializer: KSerializer<T>) :
    JsonTransformingSerializer<T>(serializer) {

    override fun transformSerialize(element: JsonElement): JsonElement = element.sorted()

}

internal fun JsonElement.sorted(): JsonElement = when (this) {
    is JsonObject -> buildJsonObject {
        for (key in keys.sorted()) {
            put(key, this@sorted[key]!!.sorted())
        }
    }
    is JsonArray -> buildJsonArray {
        this@sorted.forEach {
            add(it.sorted())
        }
    }
    else -> this@sorted
}