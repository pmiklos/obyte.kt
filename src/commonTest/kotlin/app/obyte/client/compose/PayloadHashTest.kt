package app.obyte.client.compose

import app.obyte.client.protocol.*
import kotlin.test.Test
import kotlin.test.assertEquals

class PayloadHashTest {

    @Test
    fun calculatesPayloadHash() {
        val payload = PaymentPayload(
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

        assertEquals("""
            {"inputs":[{"message_index":0,"output_index":0,"unit":"eOiQa9tbgN0Jeq5iJ4YXAT6yjlU7bpUE6Kz8TAeQiG8="},{"from_main_chain_index":1424866,"to_main_chain_index":1424887,"type":"headers_commission"},{"from_main_chain_index":105479,"to_main_chain_index":105485,"type":"witnessing"}],"outputs":[{"address":"DFVODTYGTS3ILVOQ5MFKJIERH6LGKELP","amount":232}]}
        """.trimIndent(), obyteJson.stringify(SortedSerializer(PaymentPayload.serializer()), payload))

        assertEquals("ZQfmdTThDicipdyXl4tOX6nTXxM67ckXRutx0lfjGLE=", payload.hash())
    }
}