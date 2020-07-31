package app.obyte.client.util

expect class PrivateKey(key: ByteArray) {

    val key: ByteArray

    fun toPublicKey(): PublicKey

    fun sign(message: ByteArray): ByteArray

}

class PublicKey(val key: ByteArray) {
    fun encodeBase64() = key.encodeBase64()
}

data class KeyPair(val privateKey: PrivateKey, val publicKey: PublicKey)

fun PrivateKey.encodeBase64() = key.encodeBase64()

/**
 * Generates an unpredictable seed
 */
expect fun generateSeed(): ByteArray

/**
 * @return a pair of a random private and public key
 */
expect fun keyPair(seed: ByteArray): KeyPair
