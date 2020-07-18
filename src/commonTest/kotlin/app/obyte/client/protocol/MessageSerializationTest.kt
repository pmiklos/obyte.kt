package app.obyte.client.protocol

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlin.test.Test
import kotlin.test.assertEquals

class MessageSerializationTest {

    private val json = Json(
        JsonConfiguration.Stable, context = obyteProtocol
    )

    @Test
    fun serializesJustSayingMessages() {
        assertEquals(
            """
                ["justsaying",{"subject":"version","body":{"protocol_version":"1.0t","alt":"2","library":"ocore","library_version":"0.3.12","program":"obyte-hub","program_version":"0.1.5"}}]
            """.trimIndent(),
            json.stringify(MessageSerializer, Message.JustSaying.Version(
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
            json.stringify(MessageSerializer, Message.Request.Heartbeat().apply { tag = "123" })
        )
    }

    @Test
    fun serializesSubscribedResponse() {
        assertEquals("""
            ["response",{"command":"subscribe","response":"subscribed","tag":"123"}]
        """.trimIndent(),
            json.stringify(MessageSerializer, Message.Response.Subscribed("123"))
        )
    }

    @Test
    fun serializesGetWitnessesRequest() {
        assertEquals("""
            ["request",{"command":"get_witnesses","params":{},"tag":"123"}]
        """.trimIndent(),
            json.stringify(MessageSerializer, Message.Request.GetWitnesses().apply { tag = "123" })
        )
    }

    @Test
    fun serializesGetParentsAndLastBallAndWitnessesUnit() {
        assertEquals("""
            ["request",{"command":"light/get_parents_and_last_ball_and_witness_list_unit","params":{"witnesses":["2FF7PSL7FYXVU5UIQHCVDTTPUOOG75GX","2GPBEZTAXKWEXMWCTGZALIZDNWS5B3V7"]},"tag":"123"}]
        """.trimIndent(),
            json.stringify(MessageSerializer, Message.Request.GetParentsAndLastBallAndWitnessesUnit(
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
                MessageSerializer, Message.Request.GetDefinition(
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
                MessageSerializer, Message.Request.GetDefinitionForAddress(
                    address = "2GPBEZTAXKWEXMWCTGZALIZDNWS5B3V7"
                ).apply { tag = "123" }
            )
        )
    }

}
