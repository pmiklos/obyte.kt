package app.obyte.client.compose

import app.obyte.client.ObyteException
import app.obyte.client.protocol.*
import app.obyte.client.util.encodeBase64
import io.ktor.util.date.GMTDate
import kotlinx.serialization.json.json

private const val TYPICAL_COMMISSION = 700

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

        val estimatedBytePayment = builder.buildBytePayment(TYPICAL_COMMISSION, lightProps.lastStableMcBallMci, paymentRepository)
        val assetPayments = builder.buildAssetPayments(lightProps.lastStableMcBallMci, paymentRepository)
        val dataFeed = builder.buildDataFeed()

        val messagesPlaceholder = listOf(estimatedBytePayment) + assetPayments + dataFeed

        val headersCommission = commissionStrategy.headersCommission(header)
        val payloadCommission = commissionStrategy.payloadCommission(messagesPlaceholder)
        val totalCommission = headersCommission + payloadCommission

        val finalBytePayment = builder.buildBytePayment(totalCommission, lightProps.lastStableMcBallMci, paymentRepository)

        val messages = listOf(finalBytePayment) + assetPayments + dataFeed

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
