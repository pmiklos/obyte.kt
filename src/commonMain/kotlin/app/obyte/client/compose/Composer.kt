package app.obyte.client.compose

import app.obyte.client.ObyteException
import app.obyte.client.protocol.*
import app.obyte.client.util.encodeBase64
import io.ktor.util.date.GMTDate
import kotlinx.serialization.json.json

class Composer internal constructor(
    private val configurationRepository: ConfigurationRepository,
    private val dagStateRepository: DagStateRepository,
    private val paymentRepository: PaymentRepository,
    private val commissionStrategy: CommissionStrategy,
    private val unitContentHashAlgorithm: UnitContentHashAlgorithm,
    private val unitHashAlgorithm: UnitHashAlgorithm
) {

    private val authentifierPlaceholder = json {
        "r" to "placeholderplaceholderplaceholderplaceholderplaceholderplaceholderplaceholderplaceholder"
    }

    suspend fun unit(wallet: Wallet, build: UnitBuilder.() -> Unit): ObyteUnit {
        val builder = UnitBuilder()
        builder.build()

        val from = wallet.address
        val witnesses = configurationRepository.getWitnesses()
        val lightProps = dagStateRepository.getGetParentsAndLastBallAndWitnessesUnit(witnesses)
        val storedDefinition = dagStateRepository.getDefinitionForAddress(from)

        if (!storedDefinition.isStable) {
            throw ObyteException("Definition or definition change for address $from is not stable yet")
        }

        val bytePayment = builder.bytePayment ?: BytePayment(from, 0)

        val estimatedCost = 700  // TODO estimate cost
        val estimatedByteAmount = bytePayment.amount + estimatedCost
        val spendableBytes = spendableCoins(lightProps.lastStableMcBallMci, from, estimatedByteAmount)

        val author = Author(
            address = from,
            definition = if (storedDefinition.definition == null) {
                wallet.addressDefinition
            } else {
                null
            }
        )

        val feePayingOutput = Output(
            address = from,
            amount = spendableBytes.totalAmount - bytePayment.amount
        )

        val bytePaymentOutput = Output(
            address = bytePayment.to,
            amount = bytePayment.amount
        )

        val bytePaymentPayload = PaymentPayload(
            inputs = spendableBytes.inputsWithProof.map { it.input },
            outputs = outputsOf(feePayingOutput, bytePaymentOutput)
        )

        val bytePaymentMessage = Message.Payment(
            payloadLocation = PayloadLocation.INLINE,
            payload = bytePaymentPayload,
            payloadHash = bytePaymentPayload.hash()
        )

        val assetPaymentMessages = builder.assetPayments.map { assetPayment ->
            val spendableAssets =
                spendableCoins(lightProps.lastStableMcBallMci, from, assetPayment.amount, assetPayment.asset)

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

        val header = ObyteUnitHeader(
            version = "3.0t",
            alt = "2",
            authors = listOf(author),
            timestamp = GMTDate().timestamp / 1000,
            lastBall = lightProps.lastStableMcBall,
            lastBallUnit = lightProps.lastStableMcBallUnit,
            witnessListUnit = lightProps.witnessListUnit,
            parentUnits = lightProps.parentUnits
        )

        val messagesPlaceholder = listOf(bytePaymentMessage) + assetPaymentMessages

        val headersCommission = commissionStrategy.headersCommission(
            header.copy(
                authors = listOf(
                    author.copy(
                        authentifiers = authentifierPlaceholder
                    )
                )
            )
        )

        val payloadCommission = commissionStrategy.payloadCommission(messagesPlaceholder)

        val finalPayload = bytePaymentPayload.copy(
            outputs = outputsOf(
                feePayingOutput.copy(
                    amount = feePayingOutput.amount - headersCommission - payloadCommission
                ),
                bytePaymentOutput
            )
        )

        val finalPayment = bytePaymentMessage.copy(
            payload = finalPayload,
            payloadHash = finalPayload.hash()
        )

        val messages = listOf(finalPayment) + assetPaymentMessages

        val contentHashToSign = unitContentHashAlgorithm.calculate(header, messages)
        val signature = wallet.sign(contentHashToSign)
        val signedAuthor = author.copy(
            authentifiers = json {
                "r" to signature.encodeBase64()
            }
        )

        val unit = ObyteUnit(
            version = header.version,
            alt = header.alt,
            authors = listOf(signedAuthor),
            timestamp = header.timestamp,
            lastBall = header.lastBall,
            lastBallUnit = header.lastBallUnit,
            witnessListUnit = header.witnessListUnit,
            parentUnits = header.parentUnits,
            headersCommission = headersCommission,
            payloadCommission = payloadCommission,
            mainChainIndex = lightProps.lastStableMcBallMci,
            messages = messages,
            unit = unitHashPlaceholder
        )

        return unit.copy(
            unit = unitHashAlgorithm.calculate(unit)
        )
    }

    private suspend fun spendableCoins(
        lastBallMci: Long,
        address: Address,
        targetAmount: Long,
        asset: UnitHash? = null
    ) =
        paymentRepository.pickDivisibleCoinsForAmount(
            Request.PickDivisibleCoinsForAmount(
                addresses = listOf(address),
                amount = targetAmount,
                asset = asset,
                lastBallMci = lastBallMci,
                spendUnconfirmed = SpendUnconfirmed.OWN
            )
        )

    private fun outputsOf(vararg outputs: Output) = listOf(*outputs).filter { it.amount != 0L }.sortedWith(OutputSorter)

}
