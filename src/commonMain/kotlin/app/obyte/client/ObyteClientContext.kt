package app.obyte.client

import app.obyte.client.protocol.Message
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.withTimeout


interface ObyteClientContext {
    suspend fun send(message: Message.JustSaying)
    suspend fun request(request: Message.Request): Message.Response?
}

class ObyteClientContextImpl internal constructor(
    private val obyteConnection: ObyteConnection,
    private val responseChannel: BroadcastChannel<Message.Response>
) : ObyteClientContext {

    override suspend fun send(message: Message.JustSaying) {
        obyteConnection.send(message)
    }

    override suspend fun request(request: Message.Request): Message.Response? = withTimeout(30000L) {
        val subscription = responseChannel.openSubscription()

        obyteConnection.send(request)

        for (response in subscription) {
            if (response.tag == request.tag) {
                subscription.cancel()
                return@withTimeout response
            }
        }
        return@withTimeout null
    }

}

class ObyteRequestContext internal constructor(
    private val obyteConnection: ObyteConnection,
    context: ObyteClientContext
) : ObyteClientContext by context {

    suspend fun respond(message: Message.Response) {
        obyteConnection.send(message)
    }

}

suspend inline fun ObyteRequestContext.subscribe(tag: String) = respond(Message.Response.Subscribed(tag))
suspend inline fun ObyteRequestContext.heartbeat() = request(Message.Request.Heartbeat()) as Message.Response.Heartbeat