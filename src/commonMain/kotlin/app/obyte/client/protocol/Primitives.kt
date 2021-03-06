package app.obyte.client.protocol

import kotlinx.serialization.*
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder


@Serializable
data class Address(val value: String) {
    @Serializer(forClass = Address::class)
    companion object : KSerializer<Address> {
        override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Address", PrimitiveKind.STRING)

        override fun serialize(encoder: Encoder, value: Address) {
            encoder.encodeString(value.value)
        }

        override fun deserialize(decoder: Decoder) = Address(decoder.decodeString())
    }
}

@Serializable
data class UnitHash(val value: String) {
    @Serializer(forClass = UnitHash::class)
    companion object : KSerializer<UnitHash> {
        override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("UnitHash", PrimitiveKind.STRING)

        override fun serialize(encoder: Encoder, value: UnitHash) {
            encoder.encodeString(value.value)
        }

        override fun deserialize(decoder: Decoder) = UnitHash(decoder.decodeString())
    }
}

internal val unitHashPlaceholder = UnitHash("placeholderplaceholderplaceholderplaceholder")