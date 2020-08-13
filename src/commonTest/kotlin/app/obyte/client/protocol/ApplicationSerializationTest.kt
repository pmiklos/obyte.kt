package app.obyte.client.protocol

import kotlinx.serialization.PolymorphicSerializer
import kotlin.test.Test
import kotlin.test.assertEquals

class ApplicationSerializationTest {

    private val json = obyteJson

    @Test
    fun serializesPaymentMessage() {
        assertEquals(
            """
            {"app":"payment","payload_location":"inline","payload_hash":"abcdef","payload":{"inputs":[{"unit":"abcdef","message_index":0,"output_index":1}],"outputs":[{"address":"ABCDEF","amount":123}]}}
        """.trimIndent(),
            json.stringify(
                PolymorphicSerializer(Message::class), Message.Payment(
                    payloadLocation = PayloadLocation.INLINE,
                    payloadHash = "abcdef",
                    payload = PaymentPayload(
                        inputs = listOf(
                            Input(
                                unit = UnitHash("abcdef"),
                                messageIndex = 0,
                                outputIndex = 1
                            )
                        ),
                        outputs = listOf(
                            Output(
                                address = Address("ABCDEF"),
                                amount = 123
                            )
                        )
                    )
                )
            )
        )
    }

    @Test
    fun deserializesPaymentMessage() {
        assertEquals(
            Message.Payment(
                payloadLocation = PayloadLocation.INLINE,
                payloadHash = "abcdef",
                payload = PaymentPayload(
                    inputs = listOf(
                        Input(
                            unit = UnitHash("abcdef"),
                            messageIndex = 0,
                            outputIndex = 1
                        )
                    ),
                    outputs = listOf(
                        Output(
                            address = Address("ABCDEF"),
                            amount = 123
                        )
                    )
                )
            ),
            json.parse(
                PolymorphicSerializer(Message::class), """
            {"app":"payment","payload_location":"inline","payload_hash":"abcdef","payload":{"inputs":[{"unit":"abcdef","message_index":0,"output_index":1}],"outputs":[{"address":"ABCDEF","amount":123}]}}
        """.trimIndent()
            )
        )
    }

    @Test
    fun serializesDataFeedMessage() {
        assertEquals(
            """
            {"app":"data_feed","payload_location":"inline","payload_hash":"abcdef","payload":{"FEED_NAME1":"value1","FEED_NAME2":"value2"}}
        """.trimIndent(),
            json.stringify(
                PolymorphicSerializer(Message::class), Message.DataFeed(
                    payloadLocation = PayloadLocation.INLINE,
                    payloadHash = "abcdef",
                    payload = mapOf(
                        "FEED_NAME1" to "value1",
                        "FEED_NAME2" to "value2"
                    )
                )
            )
        )
    }

    @Test
    fun deserializesDataFeedMessage() {
        assertEquals(
            Message.DataFeed(
                payloadLocation = PayloadLocation.INLINE,
                payloadHash = "abcdef",
                payload = mapOf(
                    "FEED_NAME1" to "value1",
                    "FEED_NAME2" to "value2"
                )
            ),
            json.parse(
                PolymorphicSerializer(Message::class), """
            {"app":"data_feed","payload_location":"inline","payload_hash":"abcdef","payload":{"FEED_NAME1":"value1","FEED_NAME2":"value2"}}
        """.trimIndent()
            )
        )
    }

}