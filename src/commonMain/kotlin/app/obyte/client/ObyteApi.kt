package app.obyte.client

import app.obyte.client.protocol.Message

suspend inline fun ObyteRequestContext.subscribe(tag: String) = respond(Message.Response.Subscribed(tag))
suspend inline fun ObyteRequestContext.heartbeat() = request(Message.Request.Heartbeat()) as? Message.Response.Heartbeat
suspend inline fun ObyteRequestContext.getWitnesses() =
    request(Message.Request.GetWitnesses()) as? Message.Response.GetWitnesses

suspend inline fun ObyteRequestContext.getGetParentsAndLastBallAndWitnessesUnit(witnesses: List<String>) =
    request(Message.Request.GetParentsAndLastBallAndWitnessesUnit(witnesses)) as? Message.Response.GetParentsAndLastBallAndWitnessesUnit

suspend inline fun ObyteRequestContext.getDefinition(address: String) =
    request(Message.Request.GetDefinition(address)) as? Message.Response.GetDefinition

suspend inline fun ObyteRequestContext.getDefinitionForAddress(address: String) =
    request(Message.Request.GetDefinitionForAddress(address)) as? Message.Response.GetDefinitionForAddress
