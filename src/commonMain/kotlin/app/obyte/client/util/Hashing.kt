package app.obyte.client.util

import io.ktor.utils.io.charsets.Charsets
import io.ktor.utils.io.core.toByteArray

internal expect fun ByteArray.sha256(): ByteArray

internal fun String.sha256(): ByteArray = toByteArray(Charsets.UTF_8).sha256()

internal expect fun ByteArray.ripemd160(): ByteArray