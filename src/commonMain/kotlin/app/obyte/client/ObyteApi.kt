package app.obyte.client

import app.obyte.client.protocol.Request
import app.obyte.client.protocol.Response

suspend inline fun ObyteRequestContext.subscribe(tag: String) = respond(Response.Subscribed(tag))
suspend inline fun ObyteRequestContext.heartbeat() = request(Request.Heartbeat()) as? Response.Heartbeat
suspend inline fun ObyteRequestContext.getWitnesses() =
    request(Request.GetWitnesses()) as? Response.GetWitnesses

suspend inline fun ObyteRequestContext.getGetParentsAndLastBallAndWitnessesUnit(witnesses: List<String>) =
    request(Request.GetParentsAndLastBallAndWitnessesUnit(witnesses)) as? Response.GetParentsAndLastBallAndWitnessesUnit

suspend inline fun ObyteRequestContext.getDefinition(address: String) =
    request(Request.GetDefinition(address)) as? Response.GetDefinition

suspend inline fun ObyteRequestContext.getDefinitionForAddress(address: String) =
    request(Request.GetDefinitionForAddress(address)) as? Response.GetDefinitionForAddress
