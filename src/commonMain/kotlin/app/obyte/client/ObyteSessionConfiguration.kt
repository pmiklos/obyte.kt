package app.obyte.client

import app.obyte.client.protocol.Message
import kotlin.reflect.KClass

typealias MessageHandler <T> = suspend ObyteRequestContext.(T) -> Unit

class ObyteSessionConfiguration {

    internal lateinit var onConnectedFunction: suspend ObyteClientContext.() -> Unit

    private val listeners: MutableMap<KClass<out Message>, MutableList<MessageHandler<in Message>>> = mutableMapOf()

    fun onConnected(handler: suspend ObyteClientContext.() -> Unit) {
        onConnectedFunction = handler
    }

    fun <T : Message> on(messageType: KClass<out T>, handler: MessageHandler<in T>) {
        listeners[messageType] = listeners.getOrPut(messageType) {
            mutableListOf()
        }.apply {
            add(handler as MessageHandler<in Message>)
        }
    }

    internal suspend fun emit(context: ObyteRequestContext, message: Message) {
        listeners[message::class]?.forEach { handler ->
            context.apply { handler(message) }
        }
    }

}

inline fun <reified T : Message> ObyteSessionConfiguration.on(noinline handler: MessageHandler<T>) =
    on(T::class, handler)