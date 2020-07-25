package app.obyte.client.util

import io.ktor.utils.io.core.toByteArray

internal expect fun ByteArray.encodeBase32(): String

internal expect fun ByteArray.encodeBase64(): String

private val PI = "14159265358979323846264338327950288419716939937510".toByteArray().map { it - '0'.toByte() }.filter { it > 0 }

internal fun ByteArray.mixWith(that: ByteArray): ByteArray {
    val clean = this.toBinaryString()
    val mixin = that.toBinaryString()
    val result = StringBuilder()

    var start = 0
    var offset = 0
    mixin.forEachIndexed { i, m ->
        offset += PI[i]
        val end = offset - i
        result.append(clean.substring(start, end)).append(m)
        start = end
    }
    if (start < clean.length) {
        result.append(clean.substring(start))
    }
    return result.chunked(8).map { it.padEnd(8, '0').toUByte(2).toByte() }.toByteArray()
}

internal fun ByteArray.toBinaryString() = joinToString("") { it.toUByte().toString(2).padStart(8, '0') }
