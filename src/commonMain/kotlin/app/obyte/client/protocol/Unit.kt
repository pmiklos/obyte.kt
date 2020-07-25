package app.obyte.client.protocol

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject

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
    val headersCommission: Int,
    @SerialName("payload_commission")
    val payloadCommission: Int,
    @SerialName("main_chain_index")
    val mainChainIndex: Long? = null,
    val unit: UnitHash
)


@Serializable
@SerialName("unit")
data class ObyteUnitHeader(
    val version: String,
    val alt: String,
    val authors: List<Author>,
    @SerialName("last_ball")
    val lastBall: UnitHash,
    @SerialName("last_ball_unit")
    val lastBallUnit: UnitHash,
    @SerialName("witness_list_unit")
    val witnessListUnit: UnitHash,
    val timestamp: Long
)

@Serializable
data class Joint(
    val unit: ObyteUnit,
    val ball: UnitHash? = null,
    @SerialName("skiplist_units")
    val skipListUnits: List<UnitHash>? = null
)

@Serializable
data class Author(
    val address: Address,
    val definition: JsonArray? = null,
    val authentifiers: JsonObject? = null
)



