package app.obyte.client.util

internal actual fun ByteArray.encodeBase64(): String {
    val buffer = js("Buffer").from(this)
    return buffer.toString("base64") as String
}
