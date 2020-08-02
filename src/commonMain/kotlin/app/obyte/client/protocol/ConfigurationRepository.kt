package app.obyte.client.protocol

interface ConfigurationRepository {

    suspend fun getWitnesses(): List<Address>

}