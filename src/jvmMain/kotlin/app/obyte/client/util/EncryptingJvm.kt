package app.obyte.client.util

import org.bouncycastle.asn1.x9.X9ECParameters
import org.bouncycastle.crypto.digests.SHA256Digest
import org.bouncycastle.crypto.ec.CustomNamedCurves
import org.bouncycastle.crypto.params.ECDomainParameters
import org.bouncycastle.crypto.params.ECPrivateKeyParameters
import org.bouncycastle.crypto.signers.ECDSASigner
import org.bouncycastle.crypto.signers.HMacDSAKCalculator
import org.bouncycastle.math.ec.FixedPointCombMultiplier
import java.math.BigInteger
import java.security.SecureRandom

private val CURVE_PARAMS: X9ECParameters = CustomNamedCurves.getByName("secp256k1")

private val CURVE = ECDomainParameters(
    CURVE_PARAMS.curve,
    CURVE_PARAMS.g,
    CURVE_PARAMS.n,
    CURVE_PARAMS.h
)
private val HALF_CURVE_ORDER = CURVE_PARAMS.n.shiftRight(1)

/**
 * @see <a href="https://github.com/web3j/web3j/blob/master/crypto/src/main/java/org/web3j/crypto/Sign.java">Ethereum Sign.java</a>
 * @see <a href="https://github.com/bitcoinj/bitcoinj/blob/master/core/src/main/java/org/bitcoinj/core/ECKey.java">BitcoinJ ECKey</a>
 */
actual class PrivateKey actual constructor(actual val key: ByteArray) {

    actual fun toPublicKey(): PublicKey {
        var privKey = BigInteger(key)

        if (privKey.bitLength() > CURVE.n.bitLength()) {
            privKey = privKey.mod(CURVE.n)
        }
        val point = FixedPointCombMultiplier().multiply(CURVE.g, privKey)

        return PublicKey(point.getEncoded(true))
    }

    actual fun sign(message: ByteArray): ByteArray {
        val signer = ECDSASigner(HMacDSAKCalculator(SHA256Digest()))
        val privKey = ECPrivateKeyParameters(BigInteger(key), CURVE)
        signer.init(true, privKey)
        val (r, s) = signer.generateSignature(message)
        return ECDSASignature(r, s).canonical.toByteArray()
    }

    data class ECDSASignature(val r: BigInteger, val s: BigInteger) {
        val canonical = if (s <= HALF_CURVE_ORDER) {
            this
        } else {
            ECDSASignature(r, CURVE.n.subtract(s))
        }
    }

    private fun ECDSASignature.toByteArray() = r.toByteArray(32) + s.toByteArray(32)

}

private val secureRandom = SecureRandom.getInstanceStrong()

/**
 * Generates an unpredictable seed
 */
actual fun generateSeed(): ByteArray = secureRandom.generateSeed(32)

/**
 * @return a pair of a private and public key based on the seed
 */
actual fun keyPair(seed: ByteArray): KeyPair {
    var privateKey = BigInteger(seed)

    if (privateKey.bitLength() > CURVE.n.bitLength()) {
        privateKey = privateKey.mod(CURVE.n)
    }

    try {
        CURVE.validatePrivateScalar(privateKey)
    } catch (e: java.lang.IllegalArgumentException) {
        throw kotlin.IllegalArgumentException("Invalid private key")
    }

    val point = FixedPointCombMultiplier().multiply(CURVE.g, privateKey)

    return KeyPair(PrivateKey(privateKey.toByteArray()), PublicKey(point.getEncoded(true)))
}

private fun BigInteger.toByteArray(size: Int): ByteArray {
    val signedBytes = this.toByteArray()
    val unsignedBytes = if (signedBytes[0] == 0.toByte()) {
        signedBytes.sliceArray(1 until signedBytes.size)
    } else {
        signedBytes
    }
    return ByteArray(size - unsignedBytes.size) + unsignedBytes
}
