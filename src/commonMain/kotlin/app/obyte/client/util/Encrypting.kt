package app.obyte.client.util

expect class PrivateKey(key: ByteArray) {

    val key: ByteArray

    fun toPublicKey(): PublicKey

}

class PublicKey(val key: ByteArray) {
    fun encodeBase64() = key.encodeBase64()
}

fun PrivateKey.encodeBase64() = key.encodeBase64()
