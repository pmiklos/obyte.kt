package app.obyte.client.compose

import app.obyte.client.protocol.*
import kotlinx.serialization.json.*
import kotlin.test.Test
import kotlin.test.assertEquals

class CommissionStrategyTest {

    @Test
    fun calculatesPayloadSize() {
        val message =
            Message.Payment(
                payloadLocation = PayloadLocation.INLINE,
                payloadHash = "jZevk/GPdOkteqYcuTrpJ+o+vPuqMNEeTGowSCQ7lMI=",
                payload = PaymentPayload(
                    inputs = listOf(
                        Input(
                            unit = UnitHash("hbb2eXbKbLo3vnCI6oOMiaTtEci14+mdOokD2UEKC/U="),
                            messageIndex = 0,
                            outputIndex = 0
                        ),
                        Input(
                            type = InputType.HEADERS_COMMISSION,
                            fromMainChainIndex = 1424473,
                            toMainChainIndex = 1424541
                        ),
                        Input(
                            type = InputType.WITNESSING,
                            fromMainChainIndex = 98310,
                            toMainChainIndex = 98317
                        )
                    ),
                    outputs = listOf(
                        Output(
                            amount = 134,
                            address = Address("O4K4QILG6VPGTYLRAI2RGYRFJZ7N2Q2O")
                        )
                    )
                )
            )

        assertEquals(406, CommissionStrategy().payloadCommission(listOf(message)))
    }

    @Test
    fun calculatesHeadersCommission() {
        val header = ObyteUnitHeader(
            version = "3.0t",
            alt = "2",
            authors = listOf(Author(
                address = Address("O4K4QILG6VPGTYLRAI2RGYRFJZ7N2Q2O"),
                authentifiers = buildJsonObject {
                    put(
                        "r",
                        JsonPrimitive("8jerSGskJS6Z0RfyMrYizkvAbdfmAz2isgwAGLckBGdGiMLzWQc2Ggk83sggHQOBo9k4S3w7oeuhaxu4n2c3Ug==")
                    )
                }
            )),
            witnessListUnit = UnitHash("TvqutGPz3T4Cs6oiChxFlclY92M2MvCvfXR5/FETato="),
            lastBall = UnitHash("Ra5HQcdAiVQiTcs6ad9qnj59FQw3+Jombr8+qm9kD38="),
            lastBallUnit = UnitHash("9brvF48oXYp9F4r+B9lPBBeT6Zj0lzUggP3d6STbWnc="),
            parentUnits = listOf(UnitHash("DQzRt3NI0IKPCL3iHrxbmGA5b63WcPEH9LaeDL0FS78=")),
            timestamp = 1595094504
        )
        assertEquals(452, CommissionStrategy().headersCommission(header))
    }


    @Test
    fun calculatesHeadersCommission2() {
        val header = ObyteUnitHeader(
            version = "3.0t",
            alt = "2",
            authors = listOf(Author(
                address = Address("U4HU6L6U46EVK3BAO3KA7EDQYAR6I77V"),
                definition = buildJsonArray {
                    add("sig")
                    add(buildJsonObject {
                        put(
                            "pubkey",
                            JsonPrimitive("Ag9QxMLvQEXSEIs/V6DmdGJ13HYr/rmmabVX9mCM23ym")
                        )
                    })
                },
                authentifiers = buildJsonObject {
                    put(
                        "r",
                        JsonPrimitive("T7Jr/5JLCDdo/qsWeS7l2IxrKo5XX8px49jxWV7rYGYobEwD4ZHtsr7zFNWCCQrCCHBjzcaBMQbkY0yNL58eug==")
                    )
                }
            )),
            witnessListUnit = UnitHash("TvqutGPz3T4Cs6oiChxFlclY92M2MvCvfXR5/FETato="),
            lastBall = UnitHash("ICJuK+UevPaiKHVyrc8LiDvdrLAB6q0daujqjP6+crc="),
            lastBallUnit = UnitHash("wveKo5If5RD2WtWDAEjc11GlkVr2fXZFpFhy7FDY6B8="),
            parentUnits = listOf(UnitHash("jMT//RkxINgQG4ArE05s65EIAk00jQK4t6cWIuiWMmY=")),
            timestamp = 1595094504
        )
        assertEquals(515, CommissionStrategy().headersCommission(header))
    }
}