package app.obyte.client.util

internal actual fun ByteArray.sha256(): ByteArray {
    val array = this
    return js("""
        var createHash = require('create-hash')
        var hashBuffer = createHash('sha256').update(array).digest()
        new Int8Array(hashBuffer)
    """) as ByteArray
}
