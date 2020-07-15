package app.obyte.client

import app.obyte.client.protocol.Message

suspend inline fun ObyteRequestContext.subscribe(tag: String) = respond(Message.Response.Subscribed(tag))
suspend inline fun ObyteRequestContext.heartbeat() = request(Message.Request.Heartbeat()) as? Message.Response.Heartbeat