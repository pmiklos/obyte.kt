package app.obyte.client.protocol

import kotlinx.serialization.*


@Serializable
data class Address(val value: String) {
    @Serializer(forClass = Address::class)
    companion object : KSerializer<Address> {
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
        override fun serialize(encoder: Encoder, value: UnitHash) {
            encoder.encodeString(value.value)
        }

        override fun deserialize(decoder: Decoder) = UnitHash(decoder.decodeString())
    }
}

internal val unitHashPlaceholder = UnitHash("placeholderplaceholderplaceholderplaceholder")