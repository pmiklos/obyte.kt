package app.obyte.client.protocol

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule

internal val messageModule = SerializersModule {
    polymorphic(Message::class) {
        Message.Payment::class with Message.Payment.serializer()
    }
}

@Serializable
sealed class Message {

    @Serializable
    @SerialName("payment")
    data class Payment(
        @SerialName("payload_location")
        val payloadLocation: PayloadLocation = PayloadLocation.INLINE,
        val payloadHash: String,
        val payload: PaymentPayload
    ) : Message()

}

@Serializable
enum class PayloadLocation {
    @SerialName("inline")
    INLINE,

    @SerialName("uri")
    URI,

    @SerialName("none")
    NONE
}

@Serializable
data class Input(
    val unit: UnitHash,
    @SerialName("message_index")
    val messageIndex: Int,
    @SerialName("output_index")
    val outputIndex: Int
)

@Serializable
data class Output(
    val address: Address,
    val amount: Long
)

@Serializable
data class PaymentPayload(
    val inputs: List<Input>,
    val outputs: List<Output>
)
