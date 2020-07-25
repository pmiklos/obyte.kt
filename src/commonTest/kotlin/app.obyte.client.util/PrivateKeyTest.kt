package app.obyte.client.util

import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals

class PrivateKeyTest {

    private val random = Random(1)

    @Test
    fun createsPublicKey() {
        val key = ByteArray(32)
        random.nextBytes(key)

        val privateKey = PrivateKey(key)
        val publicKey = privateKey.toPublicKey()

        assertEquals("Ag9QxMLvQEXSEIs/V6DmdGJ13HYr/rmmabVX9mCM23ym", publicKey.encodeBase64())
    }
}