package app.obyte.client.protocol

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonArray

@Serializable
@SerialName("unit")
data class ObyteUnit(
    val version: String,
    val alt: String,
    val messages: List<Application>,
    val authors: List<AuthorSpec>,
    @SerialName("parent_units")
    val parentUnits: List<UnitHash>,
    @SerialName("last_ball")
    val lastBall: UnitHash,
    @SerialName("last_ball_unit")
    val lastBallUnit: UnitHash,
    @SerialName("witness_list_unit")
    val witnessListUnit: UnitHash,
    val timestamp: Long
)

@Serializable
data class AuthorSpec(
    val address: Address,
    val definition: JsonArray? = null,
    val authentifiers: Map<String, String> // TODO double check if String as value is ok in all cases
)



