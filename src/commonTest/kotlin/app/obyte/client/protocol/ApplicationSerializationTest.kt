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
            {"app":"payment","payload_location":"inline","payloadHash":"abcdef","payload":{"inputs":[{"unit":"abcdef","message_index":0,"output_index":1}],"outputs":[{"address":"ABCDEF","amount":123}]}}
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
            {"app":"payment","payload_location":"inline","payloadHash":"abcdef","payload":{"inputs":[{"unit":"abcdef","message_index":0,"output_index":1}],"outputs":[{"address":"ABCDEF","amount":123}]}}
        """.trimIndent()
            )
        )
    }
}