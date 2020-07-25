package app.obyte.client

import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.security.Security

actual fun configurePlatform() {
    Security.addProvider(BouncyCastleProvider())
}
