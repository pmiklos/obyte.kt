package app.obyte.client.protocol

private val BASE64_ALPHABET: String = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/"
private val BASE64_MASK: Byte = 0x3f
private val BASE64_PAD: Char = '='

private fun Int.toBase64(): Char = BASE64_ALPHABET[this]

internal actual fun ByteArray.encodeBase64(): String {
    fun ByteArray.getOrZero(index: Int): Int = if (index >= size) 0 else get(index).toInt()
    // 4n / 3 is expected Base64 payload
    val result = StringBuilder()
    var index = 0
    while (index < size) {
        val symbolsLeft = size - index
        val padSize = if (symbolsLeft >= 3) 0 else (3 - symbolsLeft) * 8 / 6
        val chunk = (getOrZero(index) shl 16) or (getOrZero(index + 1) shl 8) or getOrZero(index + 2)
        index += 3

        for (i in 3 downTo padSize) {
            val char = (chunk shr (6 * i)) and BASE64_MASK.toInt()
            result.append(char.toBase64())
        }
        // Fill the pad with '='
        repeat(padSize) { result.append(BASE64_PAD) }
    }

    return result.toString()
}
