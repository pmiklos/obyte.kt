package app.obyte.client.util

internal fun toByteArray(buffer: dynamic): ByteArray = js("new Int8Array(buffer)") as ByteArray

external class Uint8Array(array: ByteArray)