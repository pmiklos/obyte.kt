package app.obyte.client.compose

import app.obyte.client.ObyteException
import app.obyte.client.protocol.*
import app.obyte.client.util.encodeBase64
import io.ktor.util.date.GMTDate
import kotlinx.serialization.json.json

class Composer internal constructor(
    private val wallet: Wallet,
    private val configurationRepository: ConfigurationRepository,
    private val dagStateRepository: DagStateRepository,
    private val paymentRepository: PaymentRepository,
    private val commissionStrategy: CommissionStrategy,
    private val unitContentHashAlgorithm: UnitContentHashAlgorithm,
    private val unitHashAlgorithm: UnitHashAlgorithm
) {

    suspend fun transfer(to: Address, amount: Long, asset: UnitHash? = null): ObyteUnit {
        val from = wallet.address
        val witnesses = configurationRepository.getWitnesses()
        val lightProps = dagStateRepository.getGetParentsAndLastBallAndWitnessesUnit(witnesses)
        val storedDefinition = dagStateRepository.getDefinitionForAddress(from)

        if (!storedDefinition.isStable) {
            throw ObyteException("Definition or definition change for address $from is not stable yet")
        }

        val targetAmount = amount + 700
        val coinsForAmount = paymentRepository.pickDivisibleCoinsForAmount(
            Request.PickDivisibleCoinsForAmount(
                addresses = listOf(from),
                amount = targetAmount,
                asset = asset,
                lastBallMci = lightProps.lastStableMcBallMci,
                spendUnconfirmed = SpendUnconfirmed.OWN
            )
        )

        val author = Author(
            address = from,
            definition = if (storedDefinition.definition == null) {
                wallet.addressDefinition
            } else {
                null
            }
        )

        val payload = PaymentPayload(
            inputs = coinsForAmount.inputsWithProof.map { it.input },
            outputs = listOf(
                Output(
                    address = to,
                    amount = coinsForAmount.totalAmount - amount
                )
            )
        )
        val payment = Message.Payment(
            payloadLocation = PayloadLocation.INLINE,
            payload = payload,
            payloadHash = payload.hash()
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

        val messages = listOf(payment)

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
            payloadCommission = 0,
            headersCommission = 0,
            mainChainIndex = lightProps.lastStableMcBallMci,
            messages = messages,
            unit = unitHashPlaceholder
        )

        return unit.copy(
            unit = unitHashAlgorithm.calculate(unit),
            headersCommission = commissionStrategy.headersCommission(unit.asHeader()),
            payloadCommission = commissionStrategy.payloadCommission(unit.messages)
        )
    }
}
