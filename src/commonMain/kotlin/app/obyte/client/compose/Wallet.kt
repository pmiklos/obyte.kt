package app.obyte.client.compose

import app.obyte.client.configurePlatform
import app.obyte.client.protocol.Address
import app.obyte.client.util.KeyPair
import app.obyte.client.util.PrivateKey
import app.obyte.client.util.keyPair
import app.obyte.client.util.sha256
import kotlinx.serialization.json.*
import kotlin.random.Random

class Wallet internal constructor(
    definitionHashAlgorithm: DefinitionHashAlgorithm,
    private val keyPair: KeyPair
) {

    val addressDefinition = buildJsonArray {
        add("sig")
        add(buildJsonObject {
            put("pubkey", JsonPrimitive(keyPair.publicKey.encodeBase64()))
        })
    }
    val address = Address(definitionHashAlgorithm.calculate(addressDefinition))

    companion object {
        private val random = Random(1) // TODO remove this eventually since it's not random, testing only

        init {
            configurePlatform()
        }

        fun random(): Wallet {
            val key = ByteArray(32)
            random.nextBytes(key)
            val privateKey = PrivateKey(key)
            val publicKey = privateKey.toPublicKey()
            return Wallet(DefinitionHashAlgorithm(), KeyPair(privateKey, publicKey))
        }

        fun fromSeed(seed: String) = Wallet(DefinitionHashAlgorithm(), keyPair(seed.sha256()))

    }

    fun sign(message: ByteArray): ByteArray = keyPair.privateKey.sign(message)

}