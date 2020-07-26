package app.obyte.client.compose

import app.obyte.client.protocol.Author
import app.obyte.client.protocol.ObyteUnit
import app.obyte.client.protocol.UnitHash
import app.obyte.client.util.encodeBase64
import app.obyte.client.util.sha256
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

class UnitHashAlgorithm(
    private val json: Json,
    private val contentHashAlgorithm: UnitContentHashAlgorithm) {

    fun calculate(unit: ObyteUnit): UnitHash {
        val contentHash = contentHashAlgorithm.calculate(unit.asHeader(), unit.messages)

        val strippedUnit = with(unit) {
            StrippedUnit(
                contentHash = contentHash.sha256().encodeBase64(),
                version = version,
                alt = alt,
                authors = authors.map { author -> Author(author.address) },
                parentUnits = parentUnits,
                lastBall = lastBall,
                lastBallUnit = lastBallUnit,
                witnessListUnit = witnessListUnit,
                timestamp = timestamp
            )
        }

        val unitString = json.stringify(SortedSerializer(StrippedUnit.serializer()), strippedUnit)
        return UnitHash(unitString.sha256().encodeBase64())
    }

    @SerialName("unit")
    @Serializable
    private data class StrippedUnit(
        @SerialName("content_hash")
        val contentHash: String,
        val version: String,
        val alt: String,
        val authors: List<Author>,
        @SerialName("parent_units")
        val parentUnits: List<UnitHash>? = null,
        @SerialName("last_ball")
        val lastBall: UnitHash? = null,
        @SerialName("last_ball_unit")
        val lastBallUnit: UnitHash? = null,
        @SerialName("witness_list_unit")
        val witnessListUnit: UnitHash? = null,
        val timestamp: Long
    )
}