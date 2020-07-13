# Obyte.kt

Obyte.kt is a multi-platform Kotlin library to communicate with hub nodes in the Obyte cryptocurrency network.

This project is still in a very early alpha stage.

Example usage:
```kotlin
fun main() = runBlocking {
    ObyteClient().connect(ObyteTestHub) {
        on<Message.Request.Subscribe> { request ->
            subscribe(request.tag)
            launch {
                while (true) {
                    delay(15000)
                    heartbeat()
                }
            }
        }
    }
}
```

See more examples in [obyte.kt-samples](../obyte.kt-samples)