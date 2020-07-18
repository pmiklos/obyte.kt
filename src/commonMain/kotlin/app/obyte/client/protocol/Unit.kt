package app.obyte.client.protocol

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonArray

@Serializable
@SerialName("unit")
data class ObyteUnit(
    val version: String,
    val alt: String,
    val messages: List<Message>,
    val authors: List<Author>,
    @SerialName("parent_units")
    val parentUnits: List<UnitHash>,
    @SerialName("last_ball")
    val lastBall: UnitHash,
    @SerialName("last_ball_unit")
    val lastBallUnit: UnitHash,
    @SerialName("witness_list_unit")
    val witnessListUnit: UnitHash,
    val timestamp: Long,
    @SerialName("headers_commission")
    val headerCommission: Long,
    @SerialName("payload_commission")
    val payloadCommission: Long,
    @SerialName("main_chain_index")
    val mainChainIndex: Long? = null,
    val unit: UnitHash
)

@Serializable
data class Joint(
    val unit: ObyteUnit
)

@Serializable
data class Author(
    val address: Address,
    val definition: JsonArray? = null,
    val authentifiers: Map<String, String> // TODO double check if String as value is ok in all cases
)



