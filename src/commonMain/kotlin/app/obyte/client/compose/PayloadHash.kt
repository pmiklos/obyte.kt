package app.obyte.client.compose

import app.obyte.client.protocol.PaymentPayload
import app.obyte.client.protocol.obyteJson
import app.obyte.client.util.encodeBase64
import app.obyte.client.util.sha256

fun PaymentPayload.hash(): String =
    obyteJson.stringify(SortedSerializer(PaymentPayload.serializer()), this).sha256().encodeBase64()

