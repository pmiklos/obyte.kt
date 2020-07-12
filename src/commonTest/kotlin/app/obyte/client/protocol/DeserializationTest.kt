package app.obyte.client.protocol

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
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


}