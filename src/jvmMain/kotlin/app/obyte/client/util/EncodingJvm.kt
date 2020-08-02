package app.obyte.client.util

import org.apache.commons.codec.binary.Base32
import java.util.*

private val base32 = Base32()

internal actual fun ByteArray.encodeBase64(): String = Base64.getEncoder().encodeToString(this)
internal actual fun ByteArray.encodeBase32(): String = base32.encodeAsString(this)