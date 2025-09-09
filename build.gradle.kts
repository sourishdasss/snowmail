import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm") version "2.0.10"
    alias(libs.plugins.jetbrains.compose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlin.serialization)
}

group = "ca.uwaterloo"
version = "0.10"

repositories {
    mavenCentral()    // Maven Central repository
    google()          // Google's Maven repository for AndroidX artifacts
    maven { url = uri("https://maven.pkg.jetbrains.space/public/p/compose/dev") }  // JetBrains Compose repository
}

dependencies {
    // Core dependencies
    implementation(libs.datetime)
    implementation(compose.desktop.currentOs)
    implementation(libs.json)
    implementation(libs.ktor.client.cio)
    implementation("org.slf4j:slf4j-simple:2.0.0")
    implementation("io.ktor:ktor-server-netty:2.3.4")
    implementation("io.ktor:ktor-client-cio:2.3.4")
    implementation("io.ktor:ktor-client-core:2.3.4")
    implementation("io.ktor:ktor-client-content-negotiation:2.3.4")
    implementation("io.ktor:ktor-client-serialization:2.3.4")
    implementation("io.ktor:ktor-client-plugins:2.3.4")
    implementation("io.ktor:ktor-client-logging:2.3.4")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.1")
    implementation(kotlin("stdlib-jdk8"))
    implementation(libs.pdfbox)

    // Supabase dependencies
    implementation(platform(libs.supabase.bom))
    implementation(libs.supabase.postgres)
    implementation(libs.supabase.realtime)
    implementation(libs.supabase.auth)
    implementation(libs.supabase.storage)

    // Javamail dependencies
    implementation("javax.mail:javax.mail-api:1.6.2")
    implementation("com.sun.mail:javax.mail:1.6.2")

    // Testing dependencies
    testImplementation(kotlin("test"))
    testImplementation("io.ktor:ktor-server-tests:2.3.4")
    testImplementation("org.jetbrains.kotlin:kotlin-test:1.8.0")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:1.8.0")
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation("org.mockito.kotlin:mockito-kotlin:4.1.0")
    testImplementation("org.mockito:mockito-core:4.6.1")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.4")
    testImplementation("org.junit.jupiter:junit-jupiter:5.7.0")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(20)
}

compose.desktop {
    application {
        mainClass = "ca.uwaterloo.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "Snowmail"
            packageVersion = "1.4.0"

            macOS {
                iconFile.set(project.file("src/main/resources/Icon.icns"))
            }
        }


    }
}


