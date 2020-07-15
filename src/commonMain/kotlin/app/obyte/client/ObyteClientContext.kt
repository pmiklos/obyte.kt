package app.obyte.client

import app.obyte.client.protocol.Message
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BroadcastChannel


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

    override suspend fun request(request: Message.Request): Message.Response? {
        val subscription = responseChannel.openSubscription()

        return try {
            obyteConnection.send(request)

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

class ObyteRequestContext internal constructor(
    private val obyteConnection: ObyteConnection,
    context: ObyteClientContext
) : ObyteClientContext by context {

    suspend fun respond(message: Message.Response) {
        obyteConnection.send(message)
    }

}
