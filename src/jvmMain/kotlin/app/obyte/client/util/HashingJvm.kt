package app.obyte.client.util

import java.security.MessageDigest

internal actual fun ByteArray.sha256(): ByteArray = with(MessageDigest.getInstance("SHA-256")) {
    digest(this@sha256)
}

internal actual fun ByteArray.ripemd160(): ByteArray = with(MessageDigest.getInstance("RIPEMD160")) {
    digest(this@ripemd160)
}
