package app.obyte.client.protocol

interface PaymentRepository {

    suspend fun pickDivisibleCoinsForAmount(request: Request.PickDivisibleCoinsForAmount): Response.PickDivisibleCoinsForAmount

}