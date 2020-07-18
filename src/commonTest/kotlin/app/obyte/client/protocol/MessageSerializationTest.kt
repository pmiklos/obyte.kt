package app.obyte.client.protocol

import kotlin.test.Test
import kotlin.test.assertEquals

class MessageSerializationTest {

    private val json = obyteJson

    @Test
    fun serializesJustSayingMessages() {
        assertEquals(
            """
                ["justsaying",{"subject":"version","body":{"protocol_version":"1.0t","alt":"2","library":"ocore","library_version":"0.3.12","program":"obyte-hub","program_version":"0.1.5"}}]
            """.trimIndent(),
            json.stringify(ObyteMessageSerializer, JustSaying.Version(
                protocolVersion = "1.0t",
                alt = "2",
                library = "ocore",
                libraryVersion = "0.3.12",
                program = "obyte-hub",
                programVersion = "0.1.5"
            ))
        )
    }

//    @Test
//    fun serializesRequestMessages() {
//        assertEquals(
//            "[\"request\",{\"command\":\"subscribe\",\"params\":{\"subscription_id\":\"abc\"},\"tag\":\"123\"}]",
//            json.stringify(MessageSerializer, Message.Request.Subscribe(subscriptionId = "abc", tag = "123"))
//        )
//    }


    @Test
    fun serializesHeartbeatRequest() {
        assertEquals("""
            ["request",{"command":"heartbeat","params":{},"tag":"123"}]
        """.trimIndent(),
            json.stringify(ObyteMessageSerializer, Request.Heartbeat().apply { tag = "123" })
        )
    }

    @Test
    fun serializesSubscribedResponse() {
        assertEquals("""
            ["response",{"command":"subscribe","response":"subscribed","tag":"123"}]
        """.trimIndent(),
            json.stringify(ObyteMessageSerializer, Response.Subscribed("123"))
        )
    }

    @Test
    fun serializesGetWitnessesRequest() {
        assertEquals("""
            ["request",{"command":"get_witnesses","params":{},"tag":"123"}]
        """.trimIndent(),
            json.stringify(ObyteMessageSerializer, Request.GetWitnesses().apply { tag = "123" })
        )
    }

    @Test
    fun serializesGetParentsAndLastBallAndWitnessesUnit() {
        assertEquals("""
            ["request",{"command":"light/get_parents_and_last_ball_and_witness_list_unit","params":{"witnesses":["2FF7PSL7FYXVU5UIQHCVDTTPUOOG75GX","2GPBEZTAXKWEXMWCTGZALIZDNWS5B3V7"]},"tag":"123"}]
        """.trimIndent(),
            json.stringify(ObyteMessageSerializer, Request.GetParentsAndLastBallAndWitnessesUnit(
                witnesses = listOf(
                    "2FF7PSL7FYXVU5UIQHCVDTTPUOOG75GX",
                    "2GPBEZTAXKWEXMWCTGZALIZDNWS5B3V7"
                )
            ).apply { tag = "123" })
        )
    }

    @Test
    fun serializesGetDefinition() {
        assertEquals("""
            ["request",{"command":"light/get_definition","params":"2GPBEZTAXKWEXMWCTGZALIZDNWS5B3V7","tag":"123"}]
        """.trimIndent(),
            json.stringify(
                ObyteMessageSerializer, Request.GetDefinition(
                    address = "2GPBEZTAXKWEXMWCTGZALIZDNWS5B3V7"
                ).apply { tag = "123" }
            )
        )
    }

    @Test
    fun serializesGetDefinitionForAddress() {
        assertEquals("""
            ["request",{"command":"light/get_definition_for_address","params":{"address":"2GPBEZTAXKWEXMWCTGZALIZDNWS5B3V7"},"tag":"123"}]
        """.trimIndent(),
            json.stringify(
                ObyteMessageSerializer, Request.GetDefinitionForAddress(
                    address = "2GPBEZTAXKWEXMWCTGZALIZDNWS5B3V7"
                ).apply { tag = "123" }
            )
        )
    }

    @Test
    fun serializesPostJointRequest() {
        assertEquals("""
            ["request",{"command":"post_joint","params":{"unit":{"version":"1.0","alt":"1","messages":[{"app":"payment","payload_location":"inline","payload_hash":"abcdef","payload":{"inputs":[{"unit":"abcdef","message_index":0,"output_index":1}],"outputs":[{"address":"ABCDEF","amount":123}]}}],"authors":[{"address":"ABC123","authentifiers":{"r":"3eQPIFiPVLRwBwEzxUR5th"}}],"parent_units":["B63mnJ4yNNAE+6J+L6AhQ3EY7EO1Lj7QmAM9PS8X0pg="],"last_ball":"8S2ya9lULt5abF1Z4lIJ4x5zYY9MtEALCl+jPDLsnsw=","last_ball_unit":"'bhdxFqVUut6V3N2D6Tyt+/YD6X0W+QnC95dMcJJWdtw=","witness_list_unit":"f252ZI2MN3xu8wFJ+LktVDGsay2Udzi/AUauE9ZaifY=","timestamp":12345678,"headers_commission":100,"payload_commission":200,"unit":"f252ZI2MN3xu8wFJ+LktVDGsay2Udzi/AUauE9ZaifY="}},"tag":"123"}]
        """.trimIndent(),
            json.stringify(ObyteMessageSerializer, Request.PostJoint(
                unit = ObyteUnit(
                    version = "1.0",
                    alt = "1",
                    messages = listOf(
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
                        )
                    ),
                    authors = listOf(
                        Author(
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
                    timestamp = 12345678,
                    unit = UnitHash("f252ZI2MN3xu8wFJ+LktVDGsay2Udzi/AUauE9ZaifY="),
                    headerCommission = 100,
                    payloadCommission = 200
                )
            ).apply { tag = "123" })
        )
    }

    @Test
    fun serializesGetJointRequest() {
        assertEquals("""
            ["request",{"command":"get_joint","params":"f252ZI2MN3xu8wFJ+LktVDGsay2Udzi/AUauE9ZaifY=","tag":"123"}]
        """.trimIndent(),
            json.stringify(
                ObyteMessageSerializer, Request.GetJoint(
                    unitHash = UnitHash("f252ZI2MN3xu8wFJ+LktVDGsay2Udzi/AUauE9ZaifY=")
                ).apply { tag = "123" }
            )
        )
    }

}
