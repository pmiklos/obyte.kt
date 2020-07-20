package app.obyte.client.compose

import app.obyte.client.protocol.*
import io.ktor.util.date.GMTDate

class Composer internal constructor(
    private val configurationRepository: ConfigurationRepository,
    private val dagStateRepository: DagStateRepository,
    private val paymentRepository: PaymentRepository,
    private val commissionStrategy: CommissionStrategy
) {

    suspend fun transfer(from: Address, to: Address, amount: Long, asset: UnitHash? = null) {
        val witnesses = configurationRepository.getWitnesses()

        val lightProps = dagStateRepository.getGetParentsAndLastBallAndWitnessesUnit(witnesses)

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
            authentifiers = mapOf("r" to "TODO") // TODO implement authentifiers
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
            witnessListUnit = lightProps.witnessListUnit
        )

        val messages = listOf(payment)

        val unit = ObyteUnit(
            version = "3.0t",
            alt = "2",
            payloadCommission = commissionStrategy.payloadCommission(messages),
            headersCommission = commissionStrategy.headersCommission(header),
            timestamp = GMTDate().timestamp / 1000,
            lastBall = lightProps.lastStableMcBall,
            lastBallUnit = lightProps.lastStableMcBallUnit,
            witnessListUnit = lightProps.witnessListUnit,
            parentUnits = lightProps.parentUnits,
            mainChainIndex = lightProps.lastStableMcBallMci,
            authors = listOf(author),
            messages = messages,
            unit = UnitHash("TODO")
        )

    }
}
