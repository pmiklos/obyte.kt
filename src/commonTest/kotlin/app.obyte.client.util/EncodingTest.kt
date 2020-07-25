package app.obyte.client.util

import kotlin.test.Test
import kotlin.test.assertEquals

class EncodingTest {

    @Test
    fun encodesToBinaryString() {
        assertEquals("00000000", byteArrayOf(0b00000000.toByte()).toBinaryString())
        assertEquals("10101010", byteArrayOf(0b10101010.toByte()).toBinaryString())
        assertEquals("1010101011110000", byteArrayOf(0b10101010.toByte(), 0b11110000.toByte()).toBinaryString())
    }

    @Test
    fun mixesArrayWithChecksum() {
        val clean = ByteArray(16)
        val checksum = ByteArray(4)
        clean.fill(0x00.toByte())
        checksum.fill(0xFF.toByte())

        assertEquals(
            "0100011000010000000010100000100001001000010000000100000000100000010000000010010100100000001000100000101000001000100100100000001001010000001000000001000010100000",
            clean.mixWith(checksum).toBinaryString()
        )
    }
}