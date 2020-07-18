package app.obyte.client

import app.obyte.client.protocol.ObyteMessage
import kotlin.reflect.KClass

typealias MessageHandler <T> = suspend ObyteRequestContext.(T) -> Unit

class ObyteSessionConfiguration {

    internal var onConnectedFunction: suspend ObyteClientContext.() -> Unit = {}

    private val listeners: MutableMap<KClass<out ObyteMessage>, MutableList<MessageHandler<in ObyteMessage>>> = mutableMapOf()

    fun onConnected(handler: suspend ObyteClientContext.() -> Unit) {
        onConnectedFunction = handler
    }

    fun <T : ObyteMessage> on(messageType: KClass<out T>, handler: MessageHandler<in T>) {
        listeners[messageType] = listeners.getOrPut(messageType) {
            mutableListOf()
        }.apply {
            add(handler as MessageHandler<in ObyteMessage>)
        }
    }

    internal suspend fun emit(context: ObyteRequestContext, message: ObyteMessage) {
        listeners[message::class]?.forEach { handler ->
            context.apply { handler(message) }
        }
    }

}

inline fun <reified T : ObyteMessage> ObyteSessionConfiguration.on(noinline handler: MessageHandler<T>) =
    on(T::class, handler)