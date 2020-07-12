package app.obyte.client.protocol

import kotlinx.serialization.*
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

abstract class AbstractPolymorphicMessageSerializer<T : Any> internal constructor(private val baseClass: KClass<T>) :
    KSerializer<T> {
    override fun serialize(encoder: Encoder, value: T) {
        val polymorphic = encoder.context.getPolymorphic(baseClass, value)

        encoder.encodeStructure(descriptor) {
            if (polymorphic != null) {
                encodeStringElement(descriptor, 0, polymorphic.descriptor.serialName)
                encodeSerializableElement(descriptor, 1, polymorphic as KSerializer<T>, value)

                if (value is TaggedMessage) {
                    encodeStringElement(descriptor, descriptor.getElementIndex("tag"), value.tag)
                }
            }
        }
    }

    override fun deserialize(decoder: Decoder): T {
        return deserialize(decoder) { discriminator, index ->
            val polymorphic = decoder.context.getPolymorphic(baseClass, discriminator)
            requireNotNull(polymorphic) { "Polymorphic type '$discriminator' not mapped" }
            decodeSerializableElement(descriptor, index, polymorphic as KSerializer<T>)
        }
    }

    fun deserialize(decoder: Decoder, block: CompositeDecoder.(String, Int) -> T): T =
        decoder.decodeStructure(descriptor) {
            var discriminator: String? = null
            var value: T? = null
            var tag: String? = null

            loop@ while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    CompositeDecoder.READ_DONE -> break@loop
                    0 -> discriminator = decodeStringElement(descriptor, index)
                    1 -> {
                        discriminator = requireNotNull(discriminator) {
                            "Cannot read polymorphic value before its type token"
                        }
                        value = this.block(discriminator, index)
                    }
                    2 -> tag = decodeStringElement(descriptor, index)
                    else -> throw SerializationException(
                        "Invalid index in polymorphic deserialization of " +
                                (discriminator ?: "unknown type") +
                                "\n Expected 0, 1 or READ_DONE(-1), but found $index"
                    )
                }
            }

            if (value == null) {
                discriminator = requireNotNull(discriminator) {
                    "Missing type discriminator"
                }
                val polymorphic = context.getPolymorphic(baseClass, discriminator)
                if (polymorphic != null) {
                    value = decoder.decodeNullableSerializableValue(polymorphic)
                }
            }

            if (value is TaggedMessage && descriptor.getElementIndex("tag") != CompositeDecoder.UNKNOWN_NAME) {
                requireNotNull(tag) { "Missing tag" }
                value.tag = tag
            }

            requireNotNull(value) { "Polymorphic value has not been read for class $discriminator" }
        }

}