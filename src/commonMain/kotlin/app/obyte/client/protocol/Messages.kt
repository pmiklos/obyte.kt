package app.obyte.client.protocol

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

internal val messageModule = SerializersModule {
    polymorphic(Message::class) {
        subclass(Message.Payment::class, Message.Payment.serializer())
        subclass(Message.DataFeed::class, Message.DataFeed.serializer())
    }
}

@Serializable
sealed class Message {

    @Serializable
    @SerialName("payment")
    data class Payment(
        @SerialName("payload_location")
        val payloadLocation: PayloadLocation,
        @SerialName("payload_hash")
        val payloadHash: String,
        val payload: PaymentPayload
    ) : Message()

    @Serializable
    @SerialName("data_feed")
    data class DataFeed(
        @SerialName("payload_location")
        val payloadLocation: PayloadLocation,
        @SerialName("payload_hash")
        val payloadHash: String,
        val payload: DataFeedPayload
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
enum class InputType {
    @SerialName("transfer")
    TRANSFER,

    @SerialName("headers_commission")
    HEADERS_COMMISSION,

    @SerialName("witnessing")
    WITNESSING
}

// TODO this could be polymorphic based on type, but it's not always present
@Serializable
data class Input(
    val type: InputType? = null,
    val unit: UnitHash? = null,
    @SerialName("message_index")
    val messageIndex: Int? = null,
    @SerialName("output_index")
    val outputIndex: Int? = null,
    @SerialName("from_main_chain_index")
    val fromMainChainIndex: Long? = null,
    @SerialName("to_main_chain_index")
    val toMainChainIndex: Long? = null
)

@Serializable
data class Output(
    val address: Address,
    val amount: Long
)

@Serializable
data class PaymentPayload(
    val asset: UnitHash? = null,
    val inputs: List<Input>,
    val outputs: List<Output>
)

typealias DataFeedPayload = Map<String, String>