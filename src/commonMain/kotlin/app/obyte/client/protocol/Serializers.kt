package app.obyte.client.protocol

import kotlinx.serialization.*
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlinx.serialization.modules.plus

val obyteJson = Json(
    JsonConfiguration.Stable.copy(
        classDiscriminator = "app",
        encodeDefaults = false
    ), context = protocolModule + messageModule
)

object ObyteMessageSerializer : AbstractPolymorphicMessageSerializer<ObyteMessage>(
    ObyteMessage::class
) {
    override val descriptor = SerialDescriptor("Message", StructureKind.LIST) {
        element("type", String.serializer().descriptor)
        element("message", SerialDescriptor("Message", UnionKind.CONTEXTUAL))
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
    override val descriptor = SerialDescriptor("justsaying", StructureKind.OBJECT) {
        element("subject", String.serializer().descriptor)
        element("body", SerialDescriptor("JustSaying", UnionKind.CONTEXTUAL))
    }
}

internal object RequestSerializer : AbstractPolymorphicMessageSerializer<Request>(
    Request::class
) {
    override val descriptor = SerialDescriptor("request", StructureKind.OBJECT) {
        element("command", String.serializer().descriptor)
        element("params", SerialDescriptor("Request", UnionKind.CONTEXTUAL))
        element("tag", String.serializer().descriptor)
    }
}

internal object ResponseSerializer : AbstractPolymorphicMessageSerializer<Response>(
    Response::class
) {
    override val descriptor = SerialDescriptor("response", StructureKind.OBJECT) {
        element("command", String.serializer().descriptor)
        element("response", SerialDescriptor("Response", UnionKind.CONTEXTUAL))
        element("tag", String.serializer().descriptor)
    }
}

internal class EmptyBody<T : ObyteMessage>(serialName: String, private val instance: T) : KSerializer<T> {
    @ImplicitReflectionSerializer
    override val descriptor = SerialDescriptor(serialName)
    override fun serialize(encoder: Encoder, value: T) {}
    override fun deserialize(decoder: Decoder) = instance
}
