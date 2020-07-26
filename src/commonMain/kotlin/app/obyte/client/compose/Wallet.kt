package app.obyte.client.compose

import app.obyte.client.protocol.Address
import app.obyte.client.util.PrivateKey
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.json
import kotlinx.serialization.json.jsonArray
import kotlin.random.Random

class Wallet internal constructor(
    definitionHashAlgorithm: DefinitionHashAlgorithm,
    private val privateKey: PrivateKey) {

    val publicKey = privateKey.toPublicKey()
    val addressDefinition = jsonArray {
        +"sig"
        +json {
            "pubkey" to JsonPrimitive(publicKey.encodeBase64())
        }
    }
    val address = Address(definitionHashAlgorithm.calculate(addressDefinition))

    companion object {
        private val random = Random(1) // TODO remove this eventually since it's not random, testing only

        fun random(): Wallet {
            val key = ByteArray(32)
            random.nextBytes(key)
            return Wallet(DefinitionHashAlgorithm(), PrivateKey(key))
        }

    }

    fun sign(message: ByteArray): ByteArray = privateKey.sign(message)

}