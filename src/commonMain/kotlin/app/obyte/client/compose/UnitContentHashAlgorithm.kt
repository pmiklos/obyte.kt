package app.obyte.client.compose

import app.obyte.client.protocol.Message
import app.obyte.client.protocol.ObyteUnitHeader
import app.obyte.client.util.sha256
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.list
import kotlinx.serialization.json.*

class UnitContentHashAlgorithm(val json: Json) {

    fun calculate(header: ObyteUnitHeader, messages: List<Message>): ByteArray {
        val headerElement = json.encodeToJsonElement(ObyteUnitHeader.serializer(), header)
        val messagesWithoutPayload = json.encodeToJsonElement(ListSerializer(NoPayload(Message.serializer())), messages)
        val content = headerElement.jsonObject.plus("messages" to messagesWithoutPayload)
        val contentString = json.encodeToString(JsonElement.serializer(), JsonObject(content).sorted())
        return contentString.sha256()
    }

    class NoPayload(serializer: KSerializer<Message>) : JsonTransformingSerializer<Message>(serializer) {
        override fun transformSerialize(element: JsonElement): JsonElement = if (element is JsonObject) {
            JsonObject(
                element
                    .minus("payload")
                    .minus("payload_uri")
            )
        } else {
            element
        }
    }
}
