package app.obyte.client.protocol

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule

val messageContext = SerializersModule {
    polymorphic(Application::class) {
        Application.Payment::class with Application.Payment.serializer()
    }
}

@Serializable
sealed class Application {

    @Serializable
    @SerialName("payment")
    data class Payment(
        @SerialName("payload_location")
        val payloadLocation: PayloadLocation = PayloadLocation.INLINE,
        val payloadHash: String,
        val payload: PaymentPayload
    ) : Application()

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
data class InputSpec(
    val unit: UnitHash,
    @SerialName("message_index")
    val messageIndex: Int,
    @SerialName("output_index")
    val outputIndex: Int
)

@Serializable
data class OutputSpec(
    val address: Address,
    val amount: Long
)

@Serializable
data class PaymentPayload(
    val inputs: List<InputSpec>,
    val outputs: List<OutputSpec>
)
