package app.obyte.client.protocol

import app.obyte.client.util.encodeBase64
import kotlinx.serialization.*
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.list
import kotlinx.serialization.builtins.nullable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.modules.SerializersModule
import kotlin.random.Random

internal val protocolModule = SerializersModule {
    polymorphic(JustSaying::class) {
        JustSaying.Version::class with JustSaying.Version.serializer()
        JustSaying.HubChallenge::class with JustSaying.HubChallenge.serializer()
        JustSaying.ExchangeRates::class with JustSaying.ExchangeRates.serializer()
        JustSaying.UpgradeRequired::class with EmptyBody("upgrade_required", JustSaying.UpgradeRequired)
        JustSaying.OldCore::class with EmptyBody("old core", JustSaying.OldCore)
        JustSaying.NewAddressToWatch::class with JustSaying.NewAddressToWatch.serializer()
    }
    polymorphic(Request::class) {
        Request.Subscribe::class with Request.Subscribe.serializer()
        Request.Heartbeat::class with Request.Heartbeat.serializer()
        Request.GetWitnesses::class with Request.GetWitnesses.serializer()
        Request.GetParentsAndLastBallAndWitnessesUnit::class with Request.GetParentsAndLastBallAndWitnessesUnit.serializer()
        Request.GetDefinition::class with Request.GetDefinition.serializer()
        Request.GetDefinitionForAddress::class with Request.GetDefinitionForAddress.serializer()
        Request.PostJoint::class with Request.PostJoint.serializer()
        Request.GetJoint::class with Request.GetJoint.serializer()
        Request.PickDivisibleCoinsForAmount::class with Request.PickDivisibleCoinsForAmount.serializer()
        Request.GetBalances::class with Request.GetBalances.serializer()
    }
    polymorphic(Response::class) {
        Response.Subscribed::class with Response.Subscribed.serializer()
        Response.Heartbeat::class with Response.Heartbeat.serializer()
        Response.GetWitnesses::class with Response.GetWitnesses.serializer()
        Response.GetParentsAndLastBallAndWitnessesUnit::class with Response.GetParentsAndLastBallAndWitnessesUnit.serializer()
        Response.GetDefinition::class with Response.GetDefinition.serializer()
        Response.GetDefinitionForAddress::class with Response.GetDefinitionForAddress.serializer()
        Response.PostJoint::class with Response.PostJoint.serializer()
        Response.GetJoint::class with Response.GetJoint.serializer()
        Response.PickDivisibleCoinsForAmount::class with Response.PickDivisibleCoinsForAmount.serializer()
        Response.GetBalances::class with Response.GetBalances.serializer()
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
    ): JustSaying() {
        @Serializer(forClass = NewAddressToWatch::class)
        companion object: KSerializer<NewAddressToWatch> {
            override fun serialize(encoder: Encoder, value: NewAddressToWatch) {
                encoder.encodeSerializableValue(Address.serializer(), value.address)
            }
        }
    }

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
    ): Request() {
        @Serializer(forClass = GetJoint::class)
        companion object: KSerializer<GetJoint> {
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
    ): Request()

    @Serializable
    @SerialName("light/get_balances")
    data class GetBalances(
        val addresses: List<Address>
    ): Request() {
        @Serializer(forClass = GetBalances::class)
        companion object: KSerializer<GetBalances> {
            override fun serialize(encoder: Encoder, value: GetBalances) {
                encoder.encodeSerializableValue(Address.serializer().list, value.addresses)
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
                val witnesses = decoder.decodeSerializableValue(Address.serializer().list)
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
    ): Response() {
        @Serializer(forClass = PostJoint::class)
        companion object: KSerializer<PostJoint> {
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
    ): Response()

    @Serializable
    @SerialName("light/pick_divisible_coins_for_amount")
    data class PickDivisibleCoinsForAmount(
        @SerialName("inputs_with_proofs")
        val inputsWithProof: List<InputWrapper>,
        @SerialName("total_amount")
        val totalAmount: Long,
        override var tag: String = ""
    ): Response()

    @Serializable
    @SerialName("light/get_balances")
    data class GetBalances(
        val balances: Map<Address, Map<UnitHash, Balance>>,
        override var tag: String = ""
    ): Response() {
        @Serializer(forClass = GetBalances::class)
        companion object: KSerializer<GetBalances> {
            override fun deserialize(decoder: Decoder): GetBalances {
                val balances = decoder.decodeSerializableValue(
                    MapSerializer(
                        Address.serializer(),
                        MapSerializer(UnitHash.serializer(), Balance.serializer())
                    ))
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