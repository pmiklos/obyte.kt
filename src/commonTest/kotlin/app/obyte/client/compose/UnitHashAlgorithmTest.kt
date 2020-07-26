package app.obyte.client.compose

import app.obyte.client.protocol.*
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.json
import kotlin.test.Test
import kotlin.test.assertEquals

class UnitHashAlgorithmTest {

    @Test
    fun calculatesUnitHash() {
        val unit = ObyteUnit(
            version = "3.0t",
            alt = "2",
            messages = listOf(Message.Payment(
                payloadLocation = PayloadLocation.INLINE,
                payloadHash = "ZQfmdTThDicipdyXl4tOX6nTXxM67ckXRutx0lfjGLE=",
                payload = PaymentPayload(
                    inputs = listOf(
                        Input(
                            unit = UnitHash("eOiQa9tbgN0Jeq5iJ4YXAT6yjlU7bpUE6Kz8TAeQiG8="),
                            messageIndex = 0,
                            outputIndex = 0
                        ),
                        Input(
                            type = InputType.HEADERS_COMMISSION,
                            fromMainChainIndex = 1424866,
                            toMainChainIndex = 1424887
                        ),
                        Input(
                            type = InputType.WITNESSING,
                            fromMainChainIndex = 105479,
                            toMainChainIndex = 105485
                        )
                    ),
                    outputs = listOf(
                        Output(
                            amount = 232,
                            address = Address("DFVODTYGTS3ILVOQ5MFKJIERH6LGKELP")
                        )
                    )
                )
            )),
            authors = listOf(
                Author(
                    address = Address("DFVODTYGTS3ILVOQ5MFKJIERH6LGKELP"),
                    authentifiers = json {
                        "r" to JsonPrimitive("AmXwS+cmZrh0CFnQ9uaYsuXf1PEv4yVfvP87Ru//o1o5mtI3lT7YVGvy+g5r4Y45qtcBlN8p+0Lrl5ECZd3WIw==")
                    }
                )
            ),
            timestamp = 1595134114,
            parentUnits = listOf(UnitHash("DQzRt3NI0IKPCL3iHrxbmGA5b63WcPEH9LaeDL0FS78=")),
            lastBall = UnitHash("uiFvXZ9g8CuDkQ8YgUITi5sPxWByGM+FRvHvEJxlIxg="),
            lastBallUnit = UnitHash("jMMUorJ3P0kySMFoATWZCffGWWJs1gsplyTN4yns5Cs="),
            witnessListUnit = UnitHash("TvqutGPz3T4Cs6oiChxFlclY92M2MvCvfXR5/FETato="),
            headersCommission = 452,
            payloadCommission = 406,
            unit = unitHashPlaceholder,
            mainChainIndex = 1424910
        )

        val unitContentHashAlgorithm = UnitContentHashAlgorithm(obyteJson)
        val unitHashAlgorithm = UnitHashAlgorithm(obyteJson, unitContentHashAlgorithm)

        assertEquals(UnitHash("YJI2qpj6xALKsS/cBSiMdiKbTEOd4ffIBL5JKcjnXek="), unitHashAlgorithm.calculate(unit))
    }
}