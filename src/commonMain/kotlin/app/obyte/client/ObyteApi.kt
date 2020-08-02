package app.obyte.client

import app.obyte.client.protocol.*

suspend inline fun ObyteRequestContext.subscribe(tag: String) = respond(Response.Subscribed(tag))
suspend inline fun ObyteClientContext.heartbeat() = request(Request.Heartbeat()) as? Response.Heartbeat
suspend inline fun ObyteClientContext.getWitnesses() =
    request(Request.GetWitnesses()) as? Response.GetWitnesses

suspend inline fun ObyteClientContext.getGetParentsAndLastBallAndWitnessesUnit(witnesses: List<Address>) =
    request(Request.GetParentsAndLastBallAndWitnessesUnit(witnesses)) as? Response.GetParentsAndLastBallAndWitnessesUnit

suspend inline fun ObyteClientContext.getDefinition(address: String) =
    request(Request.GetDefinition(address)) as? Response.GetDefinition

suspend inline fun ObyteClientContext.getDefinitionForAddress(address: Address) =
    request(Request.GetDefinitionForAddress(address)) as? Response.GetDefinitionForAddress

suspend inline fun ObyteClientContext.postJoint(unit: ObyteUnit) =
    request(Request.PostJoint(unit)) as? Response.PostJoint

suspend inline fun ObyteClientContext.getJoint(unitHash: UnitHash) =
    request(Request.GetJoint(unitHash)) as? Response.GetJoint

suspend inline fun ObyteClientContext.getBalances(addresses: List<Address>) =
    request(Request.GetBalances(addresses)) as? Response.GetBalances

suspend inline fun ObyteClientContext.newAddressToWatch(address: Address) =
    send(JustSaying.NewAddressToWatch(address))