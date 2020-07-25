package app.obyte.client.util

private val secp256k1 = js("require('secp256k1')")

actual class PrivateKey actual constructor(actual val key: ByteArray) {

    actual fun toPublicKey(): PublicKey {
        val privateKey = Uint8Array(key)
        val publicKey = secp256k1.publicKeyCreate(privateKey)
        return PublicKey(toByteArray(publicKey))
    }

}