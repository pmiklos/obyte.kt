package app.obyte.client.protocol

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlin.test.Test
import kotlin.test.assertEquals

class UnitSerializationTest {

    val json = Json(
        JsonConfiguration.Stable.copy(
            classDiscriminator = "app"
        ),
        context = messageContext
    )

    @Test
    fun serializesUnit() {
        assertEquals(
            """
            {"version":"1.0","alt":"1","messages":[{"app":"payment","payload_location":"inline","payloadHash":"abcdef","payload":{"inputs":[{"unit":"abcdef","message_index":0,"output_index":1}],"outputs":[{"address":"ABCDEF","amount":123}]}}],"authors":[{"address":"ABC123","definition":null,"authentifiers":{"r":"3eQPIFiPVLRwBwEzxUR5th"}}],"parent_units":["B63mnJ4yNNAE+6J+L6AhQ3EY7EO1Lj7QmAM9PS8X0pg="],"last_ball":"8S2ya9lULt5abF1Z4lIJ4x5zYY9MtEALCl+jPDLsnsw=","last_ball_unit":"'bhdxFqVUut6V3N2D6Tyt+/YD6X0W+QnC95dMcJJWdtw=","witness_list_unit":"f252ZI2MN3xu8wFJ+LktVDGsay2Udzi/AUauE9ZaifY=","timestamp":12345678}
        """.trimIndent(),
            json.stringify(
                ObyteUnit.serializer(),
                ObyteUnit(
                    version = "1.0",
                    alt = "1",
                    messages = listOf(
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
                        )
                    ),
                    authors = listOf(
                        AuthorSpec(
                            address = Address("ABC123"),
                            authentifiers = mapOf("r" to "3eQPIFiPVLRwBwEzxUR5th")
                        )
                    ),
                    parentUnits = listOf(
                        UnitHash("B63mnJ4yNNAE+6J+L6AhQ3EY7EO1Lj7QmAM9PS8X0pg=")
                    ),
                    lastBall = UnitHash("8S2ya9lULt5abF1Z4lIJ4x5zYY9MtEALCl+jPDLsnsw="),
                    lastBallUnit = UnitHash("'bhdxFqVUut6V3N2D6Tyt+/YD6X0W+QnC95dMcJJWdtw="),
                    witnessListUnit = UnitHash("f252ZI2MN3xu8wFJ+LktVDGsay2Udzi/AUauE9ZaifY="),
                    timestamp = 12345678
                )
            )
        )
    }
}