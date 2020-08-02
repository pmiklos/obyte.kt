package app.obyte.client

import app.obyte.client.compose.Composer
import app.obyte.client.protocol.JustSaying
import app.obyte.client.protocol.Request
import app.obyte.client.protocol.Response


interface ObyteClientContext {

    val composer: Composer

    suspend fun send(message: JustSaying)
    suspend fun request(request: Request): Response?
}

class ObyteClientContextImpl internal constructor(
    private val obyteConnection: ObyteConnection,
    override val composer: Composer
) : ObyteClientContext {

    override suspend fun send(message: JustSaying) {
        obyteConnection.send(message)
    }

    override suspend fun request(request: Request): Response? = obyteConnection.request(request)

}

class ObyteRequestContext internal constructor(
    private val obyteConnection: ObyteConnection,
    context: ObyteClientContext
) : ObyteClientContext by context {

    suspend fun respond(message: Response) {
        obyteConnection.send(message)
    }

}
