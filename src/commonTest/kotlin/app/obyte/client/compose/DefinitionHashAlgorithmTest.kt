package app.obyte.client.compose

import app.obyte.client.configurePlatform
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.json
import kotlinx.serialization.json.jsonArray
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class DefinitionHashAlgorithmTest {

    private val hasher = DefinitionHashAlgorithm()

    @BeforeTest
    fun init() {
        configurePlatform()
    }

    @Test
    fun hashes() {
        assertEquals("V6UTSDDH7TRP2FYZ7CHFSXDH2PTZYMAP", hasher.calculate(jsonArray {
            +"sig"
            +json {
                "pubkey" to JsonPrimitive("12345678")
            }
        }))
    }

}