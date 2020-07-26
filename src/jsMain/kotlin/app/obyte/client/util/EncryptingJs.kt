package app.obyte.client.util


@JsModule("secp256k1")
@JsNonModule
external object Secp256k1 {
    fun ecdsaSign(message: Uint8Array, privateKey: Uint8Array): EcdsaSignature
    fun publicKeyCreate(privateKey: Uint8Array): Uint8Array
}

external class EcdsaSignature {
    val signature: Uint8Array
    val recid: Int
}

actual class PrivateKey actual constructor(actual val key: ByteArray) {

    actual fun toPublicKey(): PublicKey {
        val privateKey = Uint8Array(key)
        val publicKey = Secp256k1.publicKeyCreate(privateKey)
        return PublicKey(toByteArray(publicKey))
    }

    actual fun sign(message: ByteArray): ByteArray {
        val privateKey = Uint8Array(key)
        val signature = Secp256k1.ecdsaSign(Uint8Array(message), privateKey)
        return toByteArray(signature.signature)
    }

}
