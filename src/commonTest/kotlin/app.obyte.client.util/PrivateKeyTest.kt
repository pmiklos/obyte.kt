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

    @Test
    fun signsMessage() {
        val key = ByteArray(32)
        val message = ByteArray(32)
        random.nextBytes(key)
        random.nextBytes(message)

        val privateKey = PrivateKey(key)

        assertEquals(
            "KFVY8tacyiWcUrQEa0FRBlv7ufOifMX/aX9uSTMrWbV60yyabyJoiA+pndFkcdY7twbfKRaOgbw4bGOBrkRffQ==",
            privateKey.sign(message).encodeBase64()
        )
    }

}