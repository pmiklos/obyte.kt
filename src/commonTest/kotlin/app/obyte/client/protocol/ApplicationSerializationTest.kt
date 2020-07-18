package app.obyte.client.protocol

import kotlinx.serialization.PolymorphicSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlin.test.Test
import kotlin.test.assertEquals

class ApplicationSerializationTest {

    private val json = Json(
        JsonConfiguration.Stable.copy(
            classDiscriminator = "app"
        ),
        context = messageContext
    )

    @Test
    fun serializesPaymentMessage() {
        assertEquals(
            """
            {"app":"payment","payload_location":"inline","payloadHash":"abcdef","payload":{"inputs":[{"unit":"abcdef","message_index":0,"output_index":1}],"outputs":[{"address":"ABCDEF","amount":123}]}}
        """.trimIndent(),
            json.stringify(
                PolymorphicSerializer(Application::class), Application.Payment(
                    payloadLocation = PayloadLocation.INLINE,
                    payloadHash = "abcdef",
                    payload = PaymentPayload(
                        inputs = listOf(
                            InputSpec(
                                unit = UnitHash("abcdef"),
                                messageIndex = 0,
                                outputIndex = 1
                            )
                        ),
                        outputs = listOf(
                            OutputSpec(
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
            Application.Payment(
                payloadLocation = PayloadLocation.INLINE,
                payloadHash = "abcdef",
                payload = PaymentPayload(
                    inputs = listOf(
                        InputSpec(
                            unit = UnitHash("abcdef"),
                            messageIndex = 0,
                            outputIndex = 1
                        )
                    ),
                    outputs = listOf(
                        OutputSpec(
                            address = Address("ABCDEF"),
                            amount = 123
                        )
                    )
                )
            ),
            json.parse(
                PolymorphicSerializer(Application::class), """
            {"app":"payment","payload_location":"inline","payloadHash":"abcdef","payload":{"inputs":[{"unit":"abcdef","message_index":0,"output_index":1}],"outputs":[{"address":"ABCDEF","amount":123}]}}
        """.trimIndent()
            )
        )
    }
}