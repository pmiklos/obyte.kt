val ktor_version: String by project
val kotlin_version: String by project
val coroutines_version: String by project
val serialization_version: String by project

plugins {
    kotlin("multiplatform") version "1.4.0"
    kotlin("plugin.serialization") version "1.4.0"
    id("maven-publish")
}

repositories {
    jcenter()
    mavenCentral()
}
group = "app.obyte.client"
version = "0.4.3"

kotlin {
    jvm()
    js {
        browser {
            dceTask {
                // configure DCE to prevent runtime errors in production webpack builds
                keep("ktor-ktor-io.\$\$importsForInline\$\$.ktor-ktor-io.io.ktor.utils.io")
            }
        }
        nodejs {
        }
    }

    sourceSets {
        commonMain {
            dependencies {
                implementation("io.ktor:ktor-client-websockets:$ktor_version")
                implementation("io.ktor:ktor-client-logging:$ktor_version")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutines_version")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:$serialization_version")
                implementation(kotlin("stdlib-common"))
            }
        }
        commonTest {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
        val jvmMain by getting {
            dependencies {
                api("io.ktor:ktor-client-cio:$ktor_version")
                implementation("io.ktor:ktor-client-logging-jvm:$ktor_version")
                implementation(kotlin("stdlib-jdk8"))
                implementation("commons-codec:commons-codec:1.14")
                implementation("org.bouncycastle:bcprov-jdk15on:1.66")
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(kotlin("test-junit"))
            }
        }
        val jsMain by getting {
            dependencies {
                api("io.ktor:ktor-client-js:$ktor_version")
                implementation("io.ktor:ktor-client-logging-js:$ktor_version")
                implementation(kotlin("stdlib-js"))

                implementation(npm("create-hash", "^1.2.0"))
                implementation(npm("thirty-two", "^1.0.2"))
                implementation(npm("secp256k1", "^4.0.2"))

                // declare NPM dependencies to fix bugs with ktor client build
                implementation(npm("text-encoding", "^0.7.0"))
                implementation(npm("bufferutil", "^4.0.1"))
                implementation(npm("utf-8-validate", "^5.0.2"))
                implementation(npm("abort-controller", "^3.0.0"))
                implementation(npm("fs", "^0.0.2"))
            }
        }
        val jsTest by getting {
            dependencies {
                implementation(kotlin("test-js"))
            }
        }
    }
}

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/pmiklos/obyte.kt")
            credentials {
                username = project.findProperty("gpr.user") as String? ?: System.getenv("USERNAME")
                password = project.findProperty("gpr.key") as String? ?: System.getenv("TOKEN")
            }
        }
    }
}
