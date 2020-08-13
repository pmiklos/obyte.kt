package app.obyte.client.compose

import app.obyte.client.protocol.Address
import app.obyte.client.protocol.UnitHash

class UnitBuilder internal constructor() {

    internal val assetPayments: List<AssetPayment> get() = mutableAssetPayments.toList()
    internal val dataFeed: Map<String, String> get() = mutableDataFeed.toMap()
    internal val bytePayment: BytePayment? get() = mutableBytePayment

    private var mutableBytePayment: BytePayment? = null
    private var mutableAssetPayments: MutableList<AssetPayment> = mutableListOf()
    private var mutableDataFeed: MutableMap<String, String> = mutableMapOf()

    fun payment(to: Address, amount: Long, asset: UnitHash? = null) {
        if (asset == null) {
            mutableBytePayment = BytePayment(to, amount)
        } else {
            mutableAssetPayments.add(AssetPayment(to, amount, asset))
        }
    }

    fun dataFeed(build: DataFeedBuilder.() -> Unit) {
        val builder = DataFeedBuilder()
        builder.build()
        mutableDataFeed.putAll(builder.mutableDataFeed)
    }

}

class DataFeedBuilder {

    internal var mutableDataFeed: MutableMap<String, String> = mutableMapOf()

    infix fun String.to(that: String) {
        mutableDataFeed[this] = that
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
