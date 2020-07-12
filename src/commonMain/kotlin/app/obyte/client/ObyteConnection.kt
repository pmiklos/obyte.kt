package app.obyte.client

import app.obyte.client.protocol.Message
import app.obyte.client.protocol.MessageSerializer
import io.ktor.client.features.logging.Logger
import io.ktor.client.features.websocket.DefaultClientWebSocketSession
import io.ktor.http.cio.websocket.Frame
import kotlinx.serialization.json.Json

internal class ObyteConnection(
    private val json: Json,
    private val logger: Logger,
    private val webSocketSession: DefaultClientWebSocketSession
) {
    suspend fun send(message: Message) {
        val jsonMessage = json.stringify(MessageSerializer, message)
        logger.log("OUTGOING: $jsonMessage")
        webSocketSession.send(Frame.Text(jsonMessage))
    }
}