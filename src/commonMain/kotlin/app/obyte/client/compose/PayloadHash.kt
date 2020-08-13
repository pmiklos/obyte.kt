package app.obyte.client.compose

import app.obyte.client.protocol.DataFeedPayload
import app.obyte.client.protocol.PaymentPayload
import app.obyte.client.protocol.obyteJson
import app.obyte.client.util.encodeBase64
import app.obyte.client.util.sha256
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer

fun PaymentPayload.hash(): String =
    obyteJson.stringify(SortedSerializer(PaymentPayload.serializer()), this).sha256().encodeBase64()

fun DataFeedPayload.hash(): String =
    obyteJson.stringify(SortedSerializer(MapSerializer(String.serializer(), String.serializer())), this).sha256()
        .encodeBase64()
