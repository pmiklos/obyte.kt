package app.obyte.client

import app.obyte.client.protocol.Message
import app.obyte.client.protocol.MessageSerializer
import app.obyte.client.protocol.obyteProtocol
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.features.logging.DEFAULT
import io.ktor.client.features.logging.LogLevel
import io.ktor.client.features.logging.Logger
import io.ktor.client.features.logging.Logging
import io.ktor.client.features.websocket.WebSockets
import io.ktor.client.features.websocket.wss
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.http.HttpMethod
import io.ktor.http.cio.websocket.CloseReason
import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.close
import io.ktor.http.cio.websocket.readText
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlinx.serialization.json.JsonException

fun ObyteClient(
    block: HttpClientConfig<*>.() -> Unit = {}
): HttpClient = HttpClient {
    install(WebSockets)
    install(Logging) {
        logger = Logger.DEFAULT
        level = LogLevel.HEADERS
    }
    apply(block)
}

val ObyteClientVersion = Message.JustSaying.Version(
    program = "Unknown",
    programVersion = "0.0.0",
    library = "ObyteKt",
    libraryVersion = "0.3.12",
    protocolVersion = "1.0t",
    alt = "2"
)

val ObyteTestHub: HttpRequestBuilder.() -> Unit = {
    method = HttpMethod.Get
    url {
        host = "obyte.org"
        port = 443
        path("/bb-test")
    }
}

suspend fun HttpClient.connect(
    host: String,
    port: Int = 443,
    path: String,
    block: ObyteSessionConfiguration.() -> Unit
) {
    connect({
        method = HttpMethod.Get
        url {
            this.host = host
            this.port = port
            this.path(path)
        }
    }, block)
}

suspend fun HttpClient.connect(
    request: HttpRequestBuilder.() -> Unit,
    block: ObyteSessionConfiguration.() -> Unit
) {
    val json = Json(JsonConfiguration.Stable, context = obyteProtocol)
    val logger = Logger.DEFAULT

    wss(request) {
        val responseChannel = BroadcastChannel<Message.Response>(100)
        val obyteConnection = ObyteConnection(json, logger, this)
        val obyteClientContext = ObyteClientContextImpl(obyteConnection, responseChannel)
        val obyteRequestContext = ObyteRequestContext(obyteConnection, obyteClientContext)
        val obyteSessionConfiguration = ObyteSessionConfiguration()

        obyteSessionConfiguration.apply(block)

        obyteClientContext.send(ObyteClientVersion)

        with(obyteSessionConfiguration) {
            obyteClientContext.apply { onConnectedFunction() }
        }

        loop@ for (frame in incoming) {
            when (frame) {
                is Frame.Text -> {
                    val rawMsg = frame.readText()

                    logger.log("INCOMING: $rawMsg")

                    try {
                        when (val message = json.parse(MessageSerializer, rawMsg)) {
                            is Message.Response -> responseChannel.send(message)
                            is Message.JustSaying.UpgradeRequired -> {
                                logger.log("Client library upgrade required")
                                obyteSessionConfiguration.emit(obyteRequestContext, message)
                                close(CloseReason(CloseReason.Codes.NORMAL, "Old client version"))
                                break@loop
                            }
                            else -> obyteSessionConfiguration.emit(obyteRequestContext, message)
                        }
                    } catch (e: JsonException) {
                        logger.log("Cannot parse: $rawMsg")
                        //e.printStackTrace()
                    } catch (e: SerializationException) {
                        logger.log("Cannot deserialize: $rawMsg")
                        //e.printStackTrace()
                    } catch (e: Exception) {
                        //e.printStackTrace()
                    }
                }
                else -> println("Unknown frame $frame")
            }
        }

        logger.log("Message loop stopped")
        terminate()
    }
}
