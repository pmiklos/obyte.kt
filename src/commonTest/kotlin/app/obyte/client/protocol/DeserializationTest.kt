package app.obyte.client.protocol

import kotlinx.serialization.json.*
import kotlin.test.*

class DeserializationTest {

    private val json = Json(
        JsonConfiguration.Stable, context = obyteProtocol
    )

    @Test
    fun deserializesJustSayingVersion() {
        assertEquals(
            Message.JustSaying.Version(
                protocolVersion = "1.0t",
                alt = "2",
                library = "ocore",
                libraryVersion = "0.3.12",
                program = "obyte-hub",
                programVersion = "0.1.5"
            ),
            json.parse(
                MessageSerializer, """
                ["justsaying",{"subject":"version","body":{"protocol_version":"1.0t","alt":"2","library":"ocore","library_version":"0.3.12","program":"obyte-hub","program_version":"0.1.5"}}]
            """.trimIndent()
            )
        )
    }

    @Test
    fun deserializesJustSayingHubChallenge() {
        assertEquals(
            Message.JustSaying.HubChallenge(challenge = "abc123"),
            json.parse(
                MessageSerializer, """
                ["justsaying",{"subject":"hub/challenge","body":"abc123"}]
            """.trimIndent()
            )
        )
    }

    @Test
    fun deserializesHeartbeatResponse() {
        assertEquals(
            Message.Response.Heartbeat(tag = "123"),
            json.parse(
                MessageSerializer, """
                ["response",{"command":"heartbeat","tag":"123"}]
            """.trimIndent()
            )
        )
    }

    @Test
    fun deserializesUpdgradeRequired() {
        assertTrue {
            json.parse(
                MessageSerializer, """
                ["justsaying",{"subject":"upgrade_required"}]
            """.trimIndent()
            ) is Message.JustSaying.UpgradeRequired
        }
    }

    @Test
    fun deserializesExchangeRates() {
        assertEquals(
            Message.JustSaying.ExchangeRates(
                mapOf("BTC_USD" to 9244.66929639, "GBYTE_BTC" to 0.00218934)
            ),
            json.parse(
                MessageSerializer, """
                ["justsaying",{"subject":"exchange_rates","body":{"BTC_USD":9244.66929639,"GBYTE_BTC":0.00218934}}]
            """.trimIndent()
            )
        )
    }

    @Test
    fun deserializesGetWitnessesResponse() {
        assertEquals(
            Message.Response.GetWitnesses(
                tag = "rhsIltHwAsQFK2cbfSkN+goCW7qED3juARe3m9eR7Iw=",
                witnesses = listOf(
                    "2FF7PSL7FYXVU5UIQHCVDTTPUOOG75GX",
                    "2GPBEZTAXKWEXMWCTGZALIZDNWS5B3V7",
                    "4H2AMKF6YO2IWJ5MYWJS3N7Y2YU2T4Z5",
                    "DFVODTYGTS3ILVOQ5MFKJIERH6LGKELP",
                    "ERMF7V2RLCPABMX5AMNGUQBAH4CD5TK4",
                    "F4KHJUCLJKY4JV7M5F754LAJX4EB7M4N",
                    "IOF6PTBDTLSTBS5NWHUSD7I2NHK3BQ2T",
                    "O4K4QILG6VPGTYLRAI2RGYRFJZ7N2Q2O",
                    "OPNUXBRSSQQGHKQNEPD2GLWQYEUY5XLD",
                    "PA4QK46276MJJD5DBOLIBMYKNNXMUVDP",
                    "RJDYXC4YQ4AZKFYTJVCR5GQJF5J6KPRI",
                    "WELOXP3EOA75JWNO6S5ZJHOO3EYFKPIR"
                )
            ),
            json.parse(
                MessageSerializer, """
                    ["response",{"tag":"rhsIltHwAsQFK2cbfSkN+goCW7qED3juARe3m9eR7Iw=","command":"get_witnesses","response":["2FF7PSL7FYXVU5UIQHCVDTTPUOOG75GX","2GPBEZTAXKWEXMWCTGZALIZDNWS5B3V7","4H2AMKF6YO2IWJ5MYWJS3N7Y2YU2T4Z5","DFVODTYGTS3ILVOQ5MFKJIERH6LGKELP","ERMF7V2RLCPABMX5AMNGUQBAH4CD5TK4","F4KHJUCLJKY4JV7M5F754LAJX4EB7M4N","IOF6PTBDTLSTBS5NWHUSD7I2NHK3BQ2T","O4K4QILG6VPGTYLRAI2RGYRFJZ7N2Q2O","OPNUXBRSSQQGHKQNEPD2GLWQYEUY5XLD","PA4QK46276MJJD5DBOLIBMYKNNXMUVDP","RJDYXC4YQ4AZKFYTJVCR5GQJF5J6KPRI","WELOXP3EOA75JWNO6S5ZJHOO3EYFKPIR"]}]
            """.trimIndent()
            )
        )
    }

    @Test
    fun deserializes() {
        assertEquals(
            Message.Response.GetParentsAndLastBallAndWitnessesUnit(
                tag = "14erii/WmtUdKTJipHn/dXkg68jMlvZ6ZgOjqk/YdIQ=",
                timestamp = 1594791527,
                parentUnits = listOf("O0Y+5ay6Rp7xz7TxyB2WuL08MhbMj2jGAuXF6OhjVcI="),
                lastStableMcBall = "yc8CpinEifj8JbY41cXByyHHU3G+zjFmxV+Dc4JFWN4=",
                lastStableMcBallUnit = "wSelCzk3lTF8saQ2j1ISAXoTvD/ahTdK4uJl0UTDepk=",
                lastStableMcBallMci = 1415908,
                witnessListUnit = "TvqutGPz3T4Cs6oiChxFlclY92M2MvCvfXR5/FETato="
            ),
            json.parse(
                MessageSerializer, """
                    ["response",{"tag":"14erii/WmtUdKTJipHn/dXkg68jMlvZ6ZgOjqk/YdIQ=","command":"light/get_parents_and_last_ball_and_witness_list_unit","response":{"timestamp":1594791527,"parent_units":["O0Y+5ay6Rp7xz7TxyB2WuL08MhbMj2jGAuXF6OhjVcI="],"last_stable_mc_ball":"yc8CpinEifj8JbY41cXByyHHU3G+zjFmxV+Dc4JFWN4=","last_stable_mc_ball_unit":"wSelCzk3lTF8saQ2j1ISAXoTvD/ahTdK4uJl0UTDepk=","last_stable_mc_ball_mci":1415908,"witness_list_unit":"TvqutGPz3T4Cs6oiChxFlclY92M2MvCvfXR5/FETato="}}]
            """.trimIndent()
            )
        )
    }

    @Test
    fun deserializesGetDefinitionResponse() {
        assertEquals(
            Message.Response.GetDefinition(
                tag = "FVZmlHRQ6v/tnpYG318CZa9kSj2cRhZpqVHo3h9i2+c=",
                definition = JsonArray(listOf(
                    JsonPrimitive("sig"),
                    JsonObject(mapOf(
                        "pubkey" to JsonPrimitive("Aig4kzMri5Bu+tX/Bv3OI75qWsuilN0cAxWwN7T+Helr")
                    ))
                ))
            ),
            json.parse(
                MessageSerializer, """
                ["response",{"tag":"FVZmlHRQ6v/tnpYG318CZa9kSj2cRhZpqVHo3h9i2+c=","command":"light/get_definition","response":["sig",{"pubkey":"Aig4kzMri5Bu+tX/Bv3OI75qWsuilN0cAxWwN7T+Helr"}]}]
            """.trimIndent()
            )
        )
    }


    @Test
    fun deserializesGetDefinitionNullResponse() {
        assertEquals(
            Message.Response.GetDefinition(
                tag = "FVZmlHRQ6v/tnpYG318CZa9kSj2cRhZpqVHo3h9i2+c=",
                definition = null
            ),
            json.parse(
                MessageSerializer, """
                ["response",{"tag":"FVZmlHRQ6v/tnpYG318CZa9kSj2cRhZpqVHo3h9i2+c=","command":"light/get_definition","response":null}]
            """.trimIndent()
            )
        )
    }

    @Test
    fun deserializesGetDefinitionForAddress() {
        assertEquals(
            Message.Response.GetDefinitionForAddress(
                tag = "d3W6Q3I0SiLOsCdVYW0EP/BC6lqU0FdYq8nNMS3ZrgA=",
                definitionChash = "2FF7PSL7FYXVU5UIQHCVDTTPUOOG75GX",
                definition = JsonArray(listOf(
                    JsonPrimitive("sig"),
                    JsonObject(mapOf(
                        "pubkey" to JsonPrimitive("AqYvfx6o4sFL4qXVaBPUKMMpYkk8dYI9OFaT7N6RhGPq")
                    ))
                )),
                isStable = true
            ),
            json.parse(
                MessageSerializer, """
                ["response",{"tag":"d3W6Q3I0SiLOsCdVYW0EP/BC6lqU0FdYq8nNMS3ZrgA=","command":"light/get_definition_for_address","response":{"definition_chash":"2FF7PSL7FYXVU5UIQHCVDTTPUOOG75GX","definition":["sig",{"pubkey":"AqYvfx6o4sFL4qXVaBPUKMMpYkk8dYI9OFaT7N6RhGPq"}],"is_stable":true}}]
            """.trimIndent()
            )
        )
    }
}