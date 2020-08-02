package app.obyte.client.compose

import app.obyte.client.protocol.Address
import app.obyte.client.protocol.UnitHash

class UnitBuilder internal constructor() {

    internal var bytePayment: BytePayment? = null
    internal var assetPayments: MutableList<AssetPayment> = mutableListOf()

    fun payment(to: Address, amount: Long, asset: UnitHash? = null) {
        if (asset == null) {
            bytePayment = BytePayment(to, amount)
        } else {
            assetPayments.add(AssetPayment(to, amount, asset))
        }
    }

}

internal data class BytePayment(
    val to: Address,
    val amount: Long
)

internal data class AssetPayment(
    val to: Address,
    val amount: Long,
    val asset: UnitHash
)
