package app.obyte.client.protocol

interface DagStateRepository {

    suspend fun getGetParentsAndLastBallAndWitnessesUnit(
        witnesses: List<Address>
    ): Response.GetParentsAndLastBallAndWitnessesUnit

    suspend fun getDefinitionForAddress(address: Address): Response.GetDefinitionForAddress

}