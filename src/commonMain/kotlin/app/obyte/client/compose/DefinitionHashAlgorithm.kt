package app.obyte.client.compose

import app.obyte.client.util.encodeBase32
import app.obyte.client.util.mixWith
import app.obyte.client.util.ripemd160
import app.obyte.client.util.sha256
import io.ktor.utils.io.charsets.Charsets
import io.ktor.utils.io.core.toByteArray
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonLiteral
import kotlinx.serialization.json.JsonObject

internal class DefinitionHashAlgorithm {

    fun calculate(definition: JsonElement): String {
        val definitionString = definition.sorted().stringify()
        val hash = definitionString.toByteArray(Charsets.UTF_8).ripemd160()
        val truncatedHash = hash.copyOfRange(4, hash.size)
        val checksum = truncatedHash.sha256().sliceArray(listOf(5, 13, 21, 29))
        return truncatedHash.mixWith(checksum).encodeBase32()
    }

    private fun JsonElement.stringify(): String {
        val components = mutableListOf<String>()
        stringify(components, this)
        return components.joinToString(0.toChar().toString())
    }

    private fun stringify(components: MutableList<String>, element: JsonElement) {
        when (element) {
            is JsonArray -> {
                components.add("[")
                element.forEach { stringify(components, it) }
                components.add("]")
            }
            is JsonObject -> {
                for (key in element.keys) {
                    components.add(key)
                    stringify(components, element[key]!!)
                }
            }
            is JsonLiteral -> when {
                element.isString -> {
                    components.add("s")
                    components.add(element.content)
                }
                element.booleanOrNull != null -> {
                    components.add("b")
                    components.add(element.content)
                }
                else -> {
                    components.add("n")
                    components.add(element.content)
                }
            }
            else -> throw IllegalArgumentException("Unsupported json element $element")
        }
    }

}
