package app.obyte.client.compose

import app.obyte.client.protocol.*

class UnitBuilder internal constructor(private val wallet: Wallet) {

    private var bytePayment: BytePayment? = null
    private var assetPayments: MutableList<AssetPayment> = mutableListOf()
    private var dataFeed: MutableMap<String, String> = mutableMapOf()

    fun payment(to: Address, amount: Long, asset: UnitHash? = null) {
        if (asset == null) {
            bytePayment = BytePayment(to, amount)
        } else {
            assetPayments.add(AssetPayment(to, amount, asset))
        }
    }

    fun dataFeed(build: DataFeedBuilder.() -> Unit) {
        val builder = DataFeedBuilder()
        builder.build()
        dataFeed.putAll(builder.dataFeed)
    }

    internal suspend fun buildBytePayment(
        commission: Int,
        lastBallMci: Long,
        paymentRepository: PaymentRepository
    ): Message.Payment {
        val bytePayment = bytePayment ?: BytePayment(wallet.address, 0)

        val spendableBytes =
            paymentRepository.pickDivisibleCoinsForAmount(
                Request.PickDivisibleCoinsForAmount(
                    addresses = listOf(wallet.address),
                    amount = bytePayment.amount + commission,
                    asset = null,
                    lastBallMci = lastBallMci,
                    spendUnconfirmed = SpendUnconfirmed.OWN
                )
            )

        val changeOutput = Output(
            address = wallet.address,
            amount = spendableBytes.totalAmount - (bytePayment.amount + commission)
        )

        val bytePaymentOutput = Output(
            address = bytePayment.to,
            amount = bytePayment.amount
        )

        val bytePaymentPayload = PaymentPayload(
            inputs = spendableBytes.inputsWithProof.map { it.input },
            outputs = outputsOf(changeOutput, bytePaymentOutput)
        )

        return Message.Payment(
            payloadLocation = PayloadLocation.INLINE,
            payload = bytePaymentPayload,
            payloadHash = bytePaymentPayload.hash()
        )
    }

    internal suspend fun buildAssetPayments(
        lastBallMci: Long,
        paymentRepository: PaymentRepository
    ): List<Message.Payment> {
        return assetPayments.map { assetPayment ->
            val spendableAssets =
                paymentRepository.pickDivisibleCoinsForAmount(
                    Request.PickDivisibleCoinsForAmount(
                        addresses = listOf(wallet.address),
                        amount = assetPayment.amount,
                        asset = assetPayment.asset,
                        lastBallMci = lastBallMci,
                        spendUnconfirmed = SpendUnconfirmed.OWN
                    )
                )

            val assetPaymentOutput = Output(
                address = assetPayment.to,
                amount = assetPayment.amount
            )

            val assetPaymentPayload = PaymentPayload(
                asset = assetPayment.asset,
                inputs = spendableAssets.inputsWithProof.map { it.input },
                outputs = outputsOf(assetPaymentOutput)
            )

            Message.Payment(
                payloadLocation = PayloadLocation.INLINE,
                payload = assetPaymentPayload,
                payloadHash = assetPaymentPayload.hash()
            )
        }
    }

    internal fun buildDataFeed(): List<Message.DataFeed> {
        return if (dataFeed.isNotEmpty()) {
            listOf(
                Message.DataFeed(
                    payloadLocation = PayloadLocation.INLINE,
                    payload = dataFeed,
                    payloadHash = dataFeed.hash()
                )
            )
        } else {
            emptyList()
        }
    }

    private fun outputsOf(vararg outputs: Output) = listOf(*outputs).filter { it.amount != 0L }.sortedWith(OutputSorter)

}

class DataFeedBuilder {

    internal var dataFeed: MutableMap<String, String> = mutableMapOf()

    infix fun String.to(that: String) {
        dataFeed[this] = that
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
