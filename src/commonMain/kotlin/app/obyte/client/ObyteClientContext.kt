package app.obyte.client

import app.obyte.client.protocol.JustSaying
import app.obyte.client.protocol.Request
import app.obyte.client.protocol.Response
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BroadcastChannel


interface ObyteClientContext {
    suspend fun send(message: JustSaying)
    suspend fun request(request: Request): Response?
}

class ObyteClientContextImpl internal constructor(
    private val obyteConnection: ObyteConnection,
    private val responseChannel: BroadcastChannel<Response>
) : ObyteClientContext {

    override suspend fun send(message: JustSaying) {
        obyteConnection.send(message)
    }

    override suspend fun request(request: Request): Response? {
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

    suspend fun respond(message: Response) {
        obyteConnection.send(message)
    }

}
