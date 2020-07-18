package app.obyte.client

import app.obyte.client.protocol.*
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
import kotlinx.coroutines.launch
import kotlinx.serialization.SerializationException
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

val ObyteClientVersion = JustSaying.Version(
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
    val logger = Logger.DEFAULT

    wss(request) {
        val responseChannel = BroadcastChannel<Response>(100)
        val obyteConnection = ObyteConnection(obyteJson, logger, this)
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
                        when (val message = obyteJson.parse(ObyteMessageSerializer, rawMsg)) {
                            is Response -> responseChannel.send(message)
                            is JustSaying.UpgradeRequired -> {
                                logger.log("Client library upgrade required")
                                obyteSessionConfiguration.emit(obyteRequestContext, message)
                                close(CloseReason(CloseReason.Codes.NORMAL, "Old client version"))
                                break@loop
                            }
                            else -> launch {
                                obyteSessionConfiguration.emit(obyteRequestContext, message)
                            }
                        }
                    } catch (e: JsonException) {
                        logger.log("ERROR: ${e.message}")
                    } catch (e: SerializationException) {
                        logger.log("ERROR: ${e.message}")
                    } catch (e: Exception) {
                        logger.log("ERROR: ${e.message}")
                    }
                }
                else -> logger.log("WARN: Unknown frame $frame")
            }
        }

        logger.log("Message loop stopped")
        terminate()
    }
}
