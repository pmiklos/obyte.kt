package app.obyte.client.util

external class Buffer {
    companion object {
        fun from(array: ByteArray): Buffer
    }

    fun toString(encoding: String): String

}