package app.obyte.client

import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.security.Security

actual fun configurePlatform() {
    val bouncyCastleProvider = BouncyCastleProvider()
    if (Security.getProvider(bouncyCastleProvider.name) == null) {
        Security.addProvider(bouncyCastleProvider)
    }
}
