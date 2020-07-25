package app.obyte.client.util

private val createHash = js("require('create-hash')")

internal actual fun ByteArray.sha256(): ByteArray {
    val buffer = Buffer.from(this)
    val result = createHash("sha256").update(buffer).digest()
    return toByteArray(result)
}

internal actual fun ByteArray.ripemd160(): ByteArray {
    val buffer = Buffer.from(this)
    val result = createHash("ripemd160").update(buffer, "utf8").digest()
    return toByteArray(result)
}

