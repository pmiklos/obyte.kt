package app.obyte.client

import app.obyte.client.protocol.ObyteMessage
import app.obyte.client.protocol.ObyteMessageSerializer
import app.obyte.client.protocol.Request
import app.obyte.client.protocol.Response
import io.ktor.client.features.logging.Logger
import io.ktor.client.features.websocket.DefaultClientWebSocketSession
import io.ktor.http.cio.websocket.Frame
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.withTimeout
import kotlinx.serialization.json.Json

internal class ObyteConnection(
    private val json: Json,
    private val logger: Logger,
    private val webSocketSession: DefaultClientWebSocketSession,
    private val responseChannel: BroadcastChannel<Response>
) {

    suspend fun send(message: ObyteMessage) {
        val jsonMessage = json.encodeToString(ObyteMessageSerializer, message)
        logger.log("OUTGOING: $jsonMessage")
        webSocketSession.send(Frame.Text(jsonMessage))
    }

    suspend fun request(request: Request): Response? {
        val subscription = responseChannel.openSubscription()

        return try {
            send(request)

            withTimeout(30000L) {
                for (response in subscription) {
                    if (response.tag == request.tag) {
                        return@withTimeout response
                    }
                }
                return@withTimeout null
            }
        } catch (e: TimeoutCancellationException) {
            return null
        } finally {
            subscription.cancel()
        }
    }

}