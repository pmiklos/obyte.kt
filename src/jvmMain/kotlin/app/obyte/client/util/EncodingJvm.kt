package app.obyte.client.util

import java.util.*

internal actual fun ByteArray.encodeBase64(): String = Base64.getEncoder().encodeToString(this)
