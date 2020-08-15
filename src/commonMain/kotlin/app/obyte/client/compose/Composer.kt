package app.obyte.client.compose

import app.obyte.client.ObyteException
import app.obyte.client.protocol.*
import app.obyte.client.util.encodeBase64
import io.ktor.util.date.GMTDate
import kotlinx.serialization.json.json

private const val TYPICAL_PAYMENT_PAYLOAD_COMMISSION = 300

class Composer internal constructor(
    private val configurationRepository: ConfigurationRepository,
    private val dagStateRepository: DagStateRepository,
    private val paymentRepository: PaymentRepository,
    private val commissionStrategy: CommissionStrategy,
    private val unitContentHashAlgorithm: UnitContentHashAlgorithm,
    private val unitHashAlgorithm: UnitHashAlgorithm
) {

    suspend fun unit(wallet: Wallet, configure: UnitBuilder.() -> Unit): ObyteUnit {
        val builder = UnitBuilder(wallet)
        builder.configure()

        val from = wallet.address
        val witnesses = configurationRepository.getWitnesses()
        val lightProps = dagStateRepository.getGetParentsAndLastBallAndWitnessesUnit(witnesses)
        val storedDefinition = dagStateRepository.getDefinitionForAddress(from)

        if (!storedDefinition.isStable) {
            throw ObyteException("Definition or definition change for address $from is not stable yet")
        }

        val author = Author(
            address = from,
            definition = if (storedDefinition.definition == null) wallet.addressDefinition else null
        )

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

        val assetPayments = builder.buildAssetPayments(lightProps.lastStableMcBallMci, paymentRepository)
        val dataFeed = builder.buildDataFeed()
        val otherMessages = assetPayments + dataFeed

        val headersCommission = commissionStrategy.headersCommission(header)
        val otherPayloadCommission = commissionStrategy.payloadCommission(otherMessages)
        val estimatedCommission = headersCommission + TYPICAL_PAYMENT_PAYLOAD_COMMISSION + otherPayloadCommission
        val estimatedBytePayment = builder.buildBytePayment(estimatedCommission, lightProps.lastStableMcBallMci, paymentRepository)

        val payloadCommission = commissionStrategy.payloadCommission(listOf(estimatedBytePayment) + otherMessages)
        val totalCommission = headersCommission + payloadCommission

        val finalBytePayment = builder.buildBytePayment(totalCommission, lightProps.lastStableMcBallMci, paymentRepository)

        val messages = listOf(finalBytePayment) + otherMessages

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

}
