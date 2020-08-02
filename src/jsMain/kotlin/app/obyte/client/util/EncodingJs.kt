package app.obyte.client.util

private val base32 = js("require('thirty-two')")

internal actual fun ByteArray.encodeBase64(): String = Buffer.from(this).toString("base64")

internal actual fun ByteArray.encodeBase32(): String = base32.encode(Buffer.from(this)).toString()
