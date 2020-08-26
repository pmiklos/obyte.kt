package app.obyte.client.protocol

import kotlinx.serialization.*
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.encodeStructure
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.plus

val obyteJson = Json {
    classDiscriminator = "app"
    encodeDefaults = false
    serializersModule = protocolModule + messageModule
}

object ObyteMessageSerializer : AbstractPolymorphicMessageSerializer<ObyteMessage>(
    ObyteMessage::class
) {
    @OptIn(InternalSerializationApi::class)
    override val descriptor = buildSerialDescriptor("Message", StructureKind.LIST) {
        element("type", String.serializer().descriptor)
        element("message", buildSerialDescriptor("Message", SerialKind.CONTEXTUAL))
    }

    override fun serialize(encoder: Encoder, value: ObyteMessage) {
        encoder.encodeStructure(descriptor) {
            when (value) {
                is JustSaying -> {
                    encodeStringElement(descriptor, 0, JustSayingSerializer.descriptor.serialName)
                    encodeSerializableElement(descriptor, 1, JustSayingSerializer, value)
                }
                is Request -> {
                    encodeStringElement(descriptor, 0, RequestSerializer.descriptor.serialName)
                    encodeSerializableElement(descriptor, 1, RequestSerializer, value)
                }
                is Response -> {
                    encodeStringElement(descriptor, 0, ResponseSerializer.descriptor.serialName)
                    encodeSerializableElement(descriptor, 1, ResponseSerializer, value)
                }
            }
        }
    }

    override fun deserialize(decoder: Decoder): ObyteMessage = super.deserialize(decoder) { discriminator, index ->
        when (discriminator) {
            JustSayingSerializer.descriptor.serialName -> {
                decodeSerializableElement(descriptor, index, JustSayingSerializer)
            }
            RequestSerializer.descriptor.serialName -> {
                decodeSerializableElement(descriptor, index, RequestSerializer)
            }
            ResponseSerializer.descriptor.serialName -> {
                decodeSerializableElement(descriptor, index, ResponseSerializer)
            }
            else -> throw SerializationException("Unknown message type: $discriminator")
        }
    }
}

internal object JustSayingSerializer : AbstractPolymorphicMessageSerializer<JustSaying>(
    JustSaying::class
) {
    @OptIn(InternalSerializationApi::class)
    override val descriptor = buildClassSerialDescriptor("justsaying") {
        element("subject", String.serializer().descriptor)
        element("body", buildSerialDescriptor("JustSaying", SerialKind.CONTEXTUAL))
    }
}

internal object RequestSerializer : AbstractPolymorphicMessageSerializer<Request>(
    Request::class
) {
    @OptIn(InternalSerializationApi::class)
    override val descriptor = buildClassSerialDescriptor("request") {
        element("command", String.serializer().descriptor)
        element("params", buildSerialDescriptor("Request", SerialKind.CONTEXTUAL))
        element("tag", String.serializer().descriptor)
    }
}

internal object ResponseSerializer : AbstractPolymorphicMessageSerializer<Response>(
    Response::class
) {
    @OptIn(InternalSerializationApi::class)
    override val descriptor = buildClassSerialDescriptor("response") {
        element("command", String.serializer().descriptor)
        element("response", buildSerialDescriptor("Response", SerialKind.CONTEXTUAL))
        element("tag", String.serializer().descriptor)
    }
}

internal class EmptyBody<T : ObyteMessage>(serialName: String, private val instance: T) : KSerializer<T> {
    override val descriptor = buildClassSerialDescriptor(serialName)
    override fun serialize(encoder: Encoder, value: T) {}
    override fun deserialize(decoder: Decoder) = instance
}
