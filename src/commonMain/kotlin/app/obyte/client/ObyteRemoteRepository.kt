package app.obyte.client

import app.obyte.client.protocol.*

internal class ObyteRemoteRepository(private val connection: ObyteConnection) :
    ConfigurationRepository,
    DagStateRepository,
    PaymentRepository {

    override suspend fun getWitnesses(): List<Address> {
        val response = connection.request(Request.GetWitnesses()) as? Response.GetWitnesses
        return response?.witnesses ?: throw ObyteException("Failed to fetch witnesses")
    }

    override suspend fun getGetParentsAndLastBallAndWitnessesUnit(witnesses: List<Address>): Response.GetParentsAndLastBallAndWitnessesUnit {
        val response =
            connection.request(Request.GetParentsAndLastBallAndWitnessesUnit(witnesses)) as? Response.GetParentsAndLastBallAndWitnessesUnit
        return response ?: throw ObyteException("Failed to fetch latest state of the DAG")
    }

    override suspend fun getDefinitionForAddress(address: Address): Response.GetDefinitionForAddress {
        val response = connection.request(Request.GetDefinitionForAddress(address)) as? Response.GetDefinitionForAddress
        return response ?: throw ObyteException("Failed to fetch address definition")
    }

    override suspend fun pickDivisibleCoinsForAmount(request: Request.PickDivisibleCoinsForAmount): Response.PickDivisibleCoinsForAmount {
        val response = connection.request(request) as? Response.PickDivisibleCoinsForAmount
        return response ?: throw ObyteException("Failed to pick divisible coins")
    }

}