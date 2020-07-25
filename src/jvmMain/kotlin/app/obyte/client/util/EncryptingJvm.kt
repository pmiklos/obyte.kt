package app.obyte.client.util

import org.bouncycastle.asn1.x9.X9ECParameters
import org.bouncycastle.crypto.ec.CustomNamedCurves
import org.bouncycastle.crypto.params.ECDomainParameters
import org.bouncycastle.math.ec.FixedPointCombMultiplier
import java.math.BigInteger


actual class PrivateKey actual constructor(actual val key: ByteArray) {

    companion object {
        private val CURVE_PARAMS: X9ECParameters = CustomNamedCurves.getByName("secp256k1")

        private val CURVE = ECDomainParameters(
            CURVE_PARAMS.curve,
            CURVE_PARAMS.g,
            CURVE_PARAMS.n,
            CURVE_PARAMS.h
        )
    }

    actual fun toPublicKey(): PublicKey {
        var privKey = BigInteger(key)

        println(privKey.bitLength())
        if (privKey.bitLength() > CURVE.n.bitLength()) {
            privKey = privKey.mod(CURVE.n)
        }
        val point = FixedPointCombMultiplier().multiply(CURVE.g, privKey)

        return PublicKey(point.getEncoded(true))
    }

}