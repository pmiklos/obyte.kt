package app.obyte.client.compose

import app.obyte.client.configurePlatform
import kotlinx.serialization.json.*
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
        assertEquals("V6UTSDDH7TRP2FYZ7CHFSXDH2PTZYMAP", hasher.calculate(buildJsonArray {
            add("sig")
            add(buildJsonObject {
                put("pubkey", JsonPrimitive("12345678"))
            })
        }))
    }

}