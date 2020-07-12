val ktor_version: String by project
val kotlin_version: String by project
val coroutines_version: String by project
val serialization_version: String by project

plugins {
    kotlin("multiplatform") version "1.3.72"
    kotlin("plugin.serialization") version "1.3.72"
    id("maven-publish")
}

repositories {
    jcenter()
    mavenCentral()
}
group = "app.obyte.client"
version = "0.0.1"

//apply plugin: "maven-publish"

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
    // For ARM, should be changed to iosArm32 or iosArm64
    // For Linux, should be changed to e.g. linuxX64
    // For MacOS, should be changed to e.g. macosX64
    // For Windows, should be changed to e.g. mingwX64
    linuxX64("linux") {
        binaries {
            sharedLib()
        }
    }

    sourceSets {
        commonMain {
            dependencies {
                api("io.ktor:ktor-client-cio:$ktor_version")
                implementation("io.ktor:ktor-client-websockets:$ktor_version")
                implementation("io.ktor:ktor-client-logging:$ktor_version")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutines_version")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime-common:$serialization_version")
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
                implementation(kotlin("stdlib-jdk8"))
                implementation("io.ktor:ktor-client-logging-jvm:$ktor_version")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime:$serialization_version")
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
                implementation(kotlin("stdlib-js"))
                implementation("io.ktor:ktor-client-logging-js:$ktor_version")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime-js:$serialization_version")

                // declare NPM dependencies to fix bugs with ktor client build
                implementation(npm("text-encoding"))
                implementation(npm("bufferutil"))
                implementation(npm("utf-8-validate"))
                implementation(npm("abort-controller"))
                implementation(npm("fs"))
            }
        }
        val jsTest by getting {
            dependencies {
                implementation(kotlin("test-js"))
            }
        }
        val linuxMain by getting {
            dependencies {
                implementation("io.ktor:ktor-client-logging-native:$ktor_version")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime-native:$serialization_version")
            }
        }
        val linuxTest by getting {
        }
    }
}
