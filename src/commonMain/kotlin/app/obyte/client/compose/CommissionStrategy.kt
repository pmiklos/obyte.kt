package app.obyte.client.compose

import app.obyte.client.protocol.*
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.list
import kotlinx.serialization.json.*

class CommissionStrategy {

    private val placeholderParents = listOf(unitHashPlaceholder, unitHashPlaceholder)

    private val authentifierPlaceholder = buildJsonObject {
        put(
            "r",
            "placeholderplaceholderplaceholderplaceholderplaceholderplaceholderplaceholderplaceholder"
        )
    }

    fun headersCommission(header: ObyteUnitHeader): Int {
        val element = obyteJson.encodeToJsonElement(
            ObyteUnitHeader.serializer(), header.copy(
                parentUnits = placeholderParents,
                authors = header.authors.map { author ->
                    author.copy(
                        authentifiers = authentifierPlaceholder
                    )
                }
            )
        )
        return lengthOf(element)
    }

    fun payloadCommission(messages: List<Message>): Int {
        val element = obyteJson.encodeToJsonElement(ListSerializer(Message.serializer()), messages)
        return lengthOf(buildJsonObject {
            put("messages", element)
        })
    }

    private fun lengthOf(element: JsonElement): Int = when (element) {
        is JsonPrimitive -> when {
            element.isString -> element.content.length
            element.booleanOrNull != null -> 1
            element.intOrNull != null -> 8
            else -> 0
        }
        is JsonArray -> element.sumBy { lengthOf(it) }
        is JsonObject -> element.map { (key, value) -> key.length + lengthOf(value) }.sum()
        else -> 0
    }

}
