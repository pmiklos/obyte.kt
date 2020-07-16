package app.obyte.client.protocol

import kotlinx.serialization.*
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.list
import kotlinx.serialization.builtins.nullable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.modules.SerializersModule
import kotlin.random.Random

val obyteProtocol = SerializersModule {
    polymorphic(Message.JustSaying::class) {
        Message.JustSaying.Version::class with Message.JustSaying.Version.serializer()
        Message.JustSaying.HubChallenge::class with Message.JustSaying.HubChallenge.serializer()
        Message.JustSaying.ExchangeRates::class with Message.JustSaying.ExchangeRates.serializer()
        Message.JustSaying.UpgradeRequired::class with EmptyBody("upgrade_required", Message.JustSaying.UpgradeRequired)
        Message.JustSaying.OldCore::class with EmptyBody("old core", Message.JustSaying.OldCore)
    }
    polymorphic(Message.Request::class) {
        Message.Request.Subscribe::class with Message.Request.Subscribe.serializer()
        Message.Request.Heartbeat::class with Message.Request.Heartbeat.serializer()
        Message.Request.GetWitnesses::class with Message.Request.GetWitnesses.serializer()
        Message.Request.GetParentsAndLastBallAndWitnessesUnit::class with Message.Request.GetParentsAndLastBallAndWitnessesUnit.serializer()
        Message.Request.GetDefinition::class with Message.Request.GetDefinition.serializer()
        Message.Request.GetDefinitionForAddress::class with Message.Request.GetDefinitionForAddress.serializer()
    }
    polymorphic(Message.Response::class) {
        Message.Response.Subscribed::class with Message.Response.Subscribed.serializer()
        Message.Response.Heartbeat::class with Message.Response.Heartbeat.serializer()
        Message.Response.GetWitnesses::class with Message.Response.GetWitnesses.serializer()
        Message.Response.GetParentsAndLastBallAndWitnessesUnit::class with Message.Response.GetParentsAndLastBallAndWitnessesUnit.serializer()
        Message.Response.GetDefinition::class with Message.Response.GetDefinition.serializer()
        Message.Response.GetDefinitionForAddress::class with Message.Response.GetDefinitionForAddress.serializer()
    }
}

private val random = Random.Default

private fun ByteArray.fillRandom(): ByteArray = apply { random.nextBytes(this) }

internal expect fun ByteArray.encodeBase64(): String // Base64.getEncoder().encodeToString(this)

interface TaggedMessage {
    var tag: String
}

@Serializable(with = MessageSerializer::class)
sealed class Message {

    @Serializable(with = JustSayingSerializer::class)
    sealed class JustSaying : Message() {

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
        ): JustSaying() {
            @Serializer(forClass = ExchangeRates::class)
            companion object : KSerializer<ExchangeRates> {
                override fun deserialize(decoder: Decoder): ExchangeRates {
                    val rates = decoder.decodeSerializableValue(MapSerializer(String.serializer(), Double.serializer()))
                    return ExchangeRates(rates)
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
    sealed class Request : Message(), TaggedMessage {
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
        class GetWitnesses: Request()

        @Serializable
        @SerialName("light/get_parents_and_last_ball_and_witness_list_unit")
        data class GetParentsAndLastBallAndWitnessesUnit(
            val witnesses: List<String>
        ): Request()

        @Serializable
        @SerialName("light/get_definition")
        data class GetDefinition(
            val address: String
        ): Request() {
            @Serializer(forClass = GetDefinition::class)
            companion object: KSerializer<GetDefinition> {
                override fun serialize(encoder: Encoder, value: GetDefinition) {
                    encoder.encodeString(value.address)
                }
            }
        }

        @Serializable
        @SerialName("light/get_definition_for_address")
        data class GetDefinitionForAddress(
            val address: String
        ): Request()

    }

    @Serializable(with = ResponseSerializer::class)
    sealed class Response : Message(), TaggedMessage {

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
            val witnesses: List<String>,
            override var tag: String = ""
        ): Response() {
            @Serializer(forClass = GetWitnesses::class)
            companion object: KSerializer<GetWitnesses> {
                override fun deserialize(decoder: Decoder): GetWitnesses {
                    val witnesses = decoder.decodeSerializableValue(String.serializer().list)
                    return GetWitnesses(witnesses)
                }
            }
        }

        @Serializable
        @SerialName("light/get_parents_and_last_ball_and_witness_list_unit")
        data class GetParentsAndLastBallAndWitnessesUnit(
            val timestamp: Long,
            @SerialName("parent_units")
            val parentUnits: List<String>,
            @SerialName("last_stable_mc_ball")
            val lastStableMcBall: String,
            @SerialName("last_stable_mc_ball_unit")
            val lastStableMcBallUnit: String,
            @SerialName("last_stable_mc_ball_mci")
            val lastStableMcBallMci: Long,
            @SerialName("witness_list_unit")
            val witnessListUnit: String,
            override var tag: String = ""
        ): Response()

        @Serializable
        @SerialName("light/get_definition")
        data class GetDefinition(
            // TODO map definition out to individual types
            val definition: JsonArray?,
            override var tag: String = ""
        ): Response() {
            @Serializer(forClass = GetDefinition::class)
            companion object: KSerializer<GetDefinition> {
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
            val definition: JsonArray?,
            @SerialName("is_stable")
            val isStable: Boolean,
            override var tag: String = ""
        ): Response()

    }


}
