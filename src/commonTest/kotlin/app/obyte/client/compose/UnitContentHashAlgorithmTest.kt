package app.obyte.client.compose

import app.obyte.client.protocol.*
import kotlin.test.Test
import kotlin.test.assertEquals

class UnitContentHashAlgorithmTest {

    @Test
    fun calculatesHash() {
        val payment = Message.Payment(
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
        )

        val header = ObyteUnitHeader(
            version = "3.0t",
            alt = "2",
            authors = listOf(
                Author(
                    address = Address("DFVODTYGTS3ILVOQ5MFKJIERH6LGKELP")
                )
            ),
            timestamp = 1595134114,
            lastBall = UnitHash("uiFvXZ9g8CuDkQ8YgUITi5sPxWByGM+FRvHvEJxlIxg="),
            lastBallUnit = UnitHash("jMMUorJ3P0kySMFoATWZCffGWWJs1gsplyTN4yns5Cs="),
            witnessListUnit = UnitHash("TvqutGPz3T4Cs6oiChxFlclY92M2MvCvfXR5/FETato="),
            parentUnits = listOf(UnitHash("DQzRt3NI0IKPCL3iHrxbmGA5b63WcPEH9LaeDL0FS78="))
        )

        val unitContentHashAlgorithm = UnitContentHashAlgorithm(obyteJson)

        // {"alt":"2","authors":[{"address":"DFVODTYGTS3ILVOQ5MFKJIERH6LGKELP"}],"last_ball":"uiFvXZ9g8CuDkQ8YgUITi5sPxWByGM+FRvHvEJxlIxg=","last_ball_unit":"jMMUorJ3P0kySMFoATWZCffGWWJs1gsplyTN4yns5Cs=","messages":[{"app":"payment","payload_hash":"ZQfmdTThDicipdyXl4tOX6nTXxM67ckXRutx0lfjGLE=","payload_location":"inline"}],"parent_units":["DQzRt3NI0IKPCL3iHrxbmGA5b63WcPEH9LaeDL0FS78="],"timestamp":1595134114,"version":"3.0t","witness_list_unit":"TvqutGPz3T4Cs6oiChxFlclY92M2MvCvfXR5/FETato="}
        assertEquals(
            "a7bc0cdf672c0bbcf80148fdee66d8325b7a169404abb29a78509ff6ac33404b",
            unitContentHashAlgorithm.calculate(header, listOf(payment)).toHex()
        )
    }

    private fun ByteArray.toHex() = fold(StringBuilder()) { acc, value ->
        acc.append(value.toUByte().toString(16).padStart(2, '0'))
    }.toString()
}
