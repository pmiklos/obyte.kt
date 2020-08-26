package app.obyte.client.protocol

import app.obyte.client.util.encodeBase64
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.nullable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlin.random.Random

internal val protocolModule = SerializersModule {
    polymorphic(JustSaying::class) {
        subclass(JustSaying.Version::class, JustSaying.Version.serializer())
        subclass(JustSaying.HubChallenge::class, JustSaying.HubChallenge.serializer())
        subclass(JustSaying.ExchangeRates::class, JustSaying.ExchangeRates.serializer())
        subclass(
            JustSaying.UpgradeRequired::class,
            EmptyBody("upgrade_required", JustSaying.UpgradeRequired)
        )
        subclass(JustSaying.OldCore::class, EmptyBody("old core", JustSaying.OldCore))
        subclass(JustSaying.NewAddressToWatch::class, JustSaying.NewAddressToWatch.serializer())
        subclass(JustSaying.Info::class, JustSaying.Info.serializer())
        subclass(
            JustSaying.HaveUpdates::class,
            EmptyBody("light/have_updates", JustSaying.HaveUpdates)
        )
        subclass(JustSaying.Joint::class, JustSaying.Joint.serializer())
    }
    polymorphic(Request::class) {
        subclass(Request.Subscribe::class, Request.Subscribe.serializer())
        subclass(Request.Heartbeat::class, Request.Heartbeat.serializer())
        subclass(Request.GetWitnesses::class, Request.GetWitnesses.serializer())
        subclass(
            Request.GetParentsAndLastBallAndWitnessesUnit::class,
            Request.GetParentsAndLastBallAndWitnessesUnit.serializer()
        )
        subclass(Request.GetDefinition::class, Request.GetDefinition.serializer())
        subclass(Request.GetDefinitionForAddress::class, Request.GetDefinitionForAddress.serializer())
        subclass(Request.PostJoint::class, Request.PostJoint.serializer())
        subclass(Request.GetJoint::class, Request.GetJoint.serializer())
        subclass(Request.PickDivisibleCoinsForAmount::class, Request.PickDivisibleCoinsForAmount.serializer())
        subclass(Request.GetBalances::class, Request.GetBalances.serializer())
    }
    polymorphic(Response::class) {
        subclass(Response.Subscribed::class, Response.Subscribed.serializer())
        subclass(Response.Heartbeat::class, Response.Heartbeat.serializer())
        subclass(Response.GetWitnesses::class, Response.GetWitnesses.serializer())
        subclass(
            Response.GetParentsAndLastBallAndWitnessesUnit::class,
            Response.GetParentsAndLastBallAndWitnessesUnit.serializer()
        )
        subclass(Response.GetDefinition::class, Response.GetDefinition.serializer())
        subclass(Response.GetDefinitionForAddress::class, Response.GetDefinitionForAddress.serializer())
        subclass(Response.PostJoint::class, Response.PostJoint.serializer())
        subclass(Response.GetJoint::class, Response.GetJoint.serializer())
        subclass(Response.PickDivisibleCoinsForAmount::class, Response.PickDivisibleCoinsForAmount.serializer())
        subclass(Response.GetBalances::class, Response.GetBalances.serializer())
    }
}

private val random = Random.Default

private fun ByteArray.fillRandom(): ByteArray = apply { random.nextBytes(this) }

interface TaggedMessage {
    var tag: String
}

@Serializable(with = ObyteMessageSerializer::class)
sealed class ObyteMessage


@Serializable(with = JustSayingSerializer::class)
sealed class JustSaying : ObyteMessage() {

    @Serializable
    @SerialName("version")
    data class Version(
        @SerialName("protocol_version")
        val protocolVersion: String,
        val alt: String,
        val library: String,
        @SerialName("library_version")
        val libraryVersion: String,
        val program: String,
        @SerialName("program_version")
        val programVersion: String
    ) : JustSaying()

    @Serializable
    @SerialName("hub/challenge")
    data class HubChallenge(
        val challenge: String
    ) : JustSaying() {
        @Serializer(forClass = HubChallenge::class)
        companion object : KSerializer<HubChallenge> {
            override fun serialize(encoder: Encoder, value: HubChallenge) {
                encoder.encodeString(value.challenge)
            }

            override fun deserialize(decoder: Decoder): HubChallenge {
                return HubChallenge(challenge = decoder.decodeString())
            }
        }
    }

    @Serializable
    @SerialName("exchange_rates")
    data class ExchangeRates(
        val rates: Map<String, Double>
    ) : JustSaying() {
        @Serializer(forClass = ExchangeRates::class)
        companion object : KSerializer<ExchangeRates> {
            override fun deserialize(decoder: Decoder): ExchangeRates {
                val rates = decoder.decodeSerializableValue(
                    MapSerializer(
                        String.serializer(),
                        Double.serializer()
                    )
                )
                return ExchangeRates(rates)
            }
        }
    }

    @Serializable
    @SerialName("light/new_address_to_watch")
    data class NewAddressToWatch(
        val address: Address
    ) : JustSaying() {
        @Serializer(forClass = NewAddressToWatch::class)
        companion object : KSerializer<NewAddressToWatch> {
            override fun serialize(encoder: Encoder, value: NewAddressToWatch) {
                encoder.encodeSerializableValue(Address.serializer(), value.address)
            }
        }
    }

    @Serializable
    @SerialName("info")
    data class Info(
        val message: String
    ) : JustSaying() {
        @Serializer(forClass = Info::class)
        companion object : KSerializer<Info> {
            override fun deserialize(decoder: Decoder): Info = Info(decoder.decodeString())
        }
    }

    @Serializable
    @SerialName("joint")
    data class Joint(
        val unit: ObyteUnit
    ) : JustSaying()

    @Serializable
    @SerialName("light/have_updates")
    object HaveUpdates : JustSaying()

    @Serializable
    @SerialName("upgrade_required")
    object UpgradeRequired : JustSaying()

    @Serializable
    @SerialName("old core")
    object OldCore : JustSaying()
}

@Serializable(with = RequestSerializer::class)
sealed class Request : ObyteMessage(),
    TaggedMessage {
    override var tag: String = ByteArray(32).fillRandom().encodeBase64()

    @Serializable
    @SerialName("subscribe")
    data class Subscribe(
        @SerialName("subscription_id")
        val subscriptionId: String,
        @SerialName("last_mci")
        val lastMci: Long,
        @SerialName("library_version")
        val libraryVersion: String
    ) : Request()

    @Serializable
    @SerialName("heartbeat")
    class Heartbeat : Request()

    @Serializable
    @SerialName("get_witnesses")
    class GetWitnesses : Request()

    @Serializable
    @SerialName("light/get_parents_and_last_ball_and_witness_list_unit")
    data class GetParentsAndLastBallAndWitnessesUnit(
        val witnesses: List<Address>
    ) : Request()

    @Serializable
    @SerialName("light/get_definition")
    data class GetDefinition(
        val address: String
    ) : Request() {
        @Serializer(forClass = GetDefinition::class)
        companion object : KSerializer<GetDefinition> {
            override fun serialize(encoder: Encoder, value: GetDefinition) {
                encoder.encodeString(value.address)
            }
        }
    }

    @Serializable
    @SerialName("light/get_definition_for_address")
    data class GetDefinitionForAddress(
        val address: Address
    ) : Request()

    @Serializable
    @SerialName("post_joint")
    data class PostJoint(
        val unit: ObyteUnit
    ) : Request()

    @Serializable
    @SerialName("get_joint")
    data class GetJoint(
        val unitHash: UnitHash
    ) : Request() {
        @Serializer(forClass = GetJoint::class)
        companion object : KSerializer<GetJoint> {
            override fun serialize(encoder: Encoder, value: GetJoint) {
                encoder.encodeSerializableValue(UnitHash.serializer(), value.unitHash)
            }
        }
    }

    @Serializable
    @SerialName("light/pick_divisible_coins_for_amount")
    data class PickDivisibleCoinsForAmount(
        val addresses: List<Address>,
        @SerialName("last_ball_mci")
        val lastBallMci: Long,
        val amount: Long,
        val asset: UnitHash? = null,
        @SerialName("spend_unconfirmed")
        val spendUnconfirmed: SpendUnconfirmed
    ) : Request()

    @Serializable
    @SerialName("light/get_balances")
    data class GetBalances(
        val addresses: List<Address>
    ) : Request() {
        @Serializer(forClass = GetBalances::class)
        companion object : KSerializer<GetBalances> {
            override fun serialize(encoder: Encoder, value: GetBalances) {
                encoder.encodeSerializableValue(ListSerializer(Address.serializer()), value.addresses)
            }
        }
    }
}

@Serializable(with = ResponseSerializer::class)
sealed class Response : ObyteMessage(),
    TaggedMessage {

    @Serializable
    @SerialName("subscribe")
    data class Subscribed(override var tag: String) : Response() {
        @Serializer(forClass = Subscribed::class)
        companion object : KSerializer<Subscribed> {
            override fun serialize(encoder: Encoder, value: Subscribed) {
                encoder.encodeString("subscribed")
            }
        }
    }

    @Serializable
    @SerialName("heartbeat")
    data class Heartbeat(override var tag: String) : Response() {
        @Serializer(forClass = Heartbeat::class)
        companion object : KSerializer<Heartbeat> {
            override fun deserialize(decoder: Decoder): Heartbeat = Heartbeat("")
        }
    }

    @Serializable
    @SerialName("get_witnesses")
    data class GetWitnesses(
        val witnesses: List<Address>,
        override var tag: String = ""
    ) : Response() {
        @Serializer(forClass = GetWitnesses::class)
        companion object : KSerializer<GetWitnesses> {
            override fun deserialize(decoder: Decoder): GetWitnesses {
                val witnesses = decoder.decodeSerializableValue(ListSerializer(Address.serializer()))
                return GetWitnesses(witnesses)
            }
        }
    }

    @Serializable
    @SerialName("light/get_parents_and_last_ball_and_witness_list_unit")
    data class GetParentsAndLastBallAndWitnessesUnit(
        val timestamp: Long,
        @SerialName("parent_units")
        val parentUnits: List<UnitHash>,
        @SerialName("last_stable_mc_ball")
        val lastStableMcBall: UnitHash,
        @SerialName("last_stable_mc_ball_unit")
        val lastStableMcBallUnit: UnitHash,
        @SerialName("last_stable_mc_ball_mci")
        val lastStableMcBallMci: Long,
        @SerialName("witness_list_unit")
        val witnessListUnit: UnitHash,
        override var tag: String = ""
    ) : Response()

    @Serializable
    @SerialName("light/get_definition")
    data class GetDefinition(
        // TODO map definition out to individual types
        val definition: JsonArray?,
        override var tag: String = ""
    ) : Response() {
        @Serializer(forClass = GetDefinition::class)
        companion object : KSerializer<GetDefinition> {
            override fun deserialize(decoder: Decoder): GetDefinition {
                val definition = decoder.decodeNullableSerializableValue(JsonArray.serializer().nullable)
                return GetDefinition(definition)
            }
        }
    }

    @Serializable
    @SerialName("light/get_definition_for_address")
    data class GetDefinitionForAddress(
        @SerialName("definition_chash")
        val definitionChash: String,
        val definition: JsonArray? = null,
        @SerialName("is_stable")
        val isStable: Boolean,
        override var tag: String = ""
    ) : Response()

    @Serializable
    @SerialName("post_joint")
    data class PostJoint(
        val response: String,
        override var tag: String = ""
    ) : Response() {
        @Serializer(forClass = PostJoint::class)
        companion object : KSerializer<PostJoint> {
            override fun deserialize(decoder: Decoder): PostJoint {
                val response = decoder.decodeString()
                return PostJoint(response)
            }
        }
    }

    @Serializable
    @SerialName("get_joint")
    data class GetJoint(
        val joint: Joint,
        override var tag: String = ""
    ) : Response()

    @Serializable
    @SerialName("light/pick_divisible_coins_for_amount")
    data class PickDivisibleCoinsForAmount(
        @SerialName("inputs_with_proofs")
        val inputsWithProof: List<InputWrapper>,
        @SerialName("total_amount")
        val totalAmount: Long,
        override var tag: String = ""
    ) : Response()

    @Serializable
    @SerialName("light/get_balances")
    data class GetBalances(
        val balances: Map<Address, Map<UnitHash, Balance>>,
        override var tag: String = ""
    ) : Response() {
        @Serializer(forClass = GetBalances::class)
        companion object : KSerializer<GetBalances> {
            override fun deserialize(decoder: Decoder): GetBalances {
                val balances = decoder.decodeSerializableValue(
                    MapSerializer(
                        Address.serializer(),
                        MapSerializer(UnitHash.serializer(), Balance.serializer())
                    )
                )
                return GetBalances(balances)
            }
        }
    }
}

@Serializable
enum class SpendUnconfirmed {
    @SerialName("all")
    ALL,

    @SerialName("own")
    OWN,

    @SerialName("none")
    NONE
}

@Serializable
data class InputWrapper(
    val input: Input
)

@Serializable
data class Balance(
    val stable: Long,
    val pending: Long,
    @SerialName("stable_outputs_count")
    val stableOutputsCount: Int,
    @SerialName("pending_outputs_count")
    val pendingOutputsCount: Int
)