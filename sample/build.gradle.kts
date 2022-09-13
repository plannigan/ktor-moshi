import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    kotlin("kapt")
    application
    id("com.github.johnrengelman.shadow")
}

application {
    mainClass.set("MainKt")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

dependencies {
    implementation(project(":core"))
    implementation(Deps.Ktor.netty)
    implementation(Deps.Ktor.serverCallLogging)
    implementation(Deps.Ktor.serverContentNegotiation)
    implementation(Deps.Ktor.clientCore)
    implementation(Deps.Ktor.clientCIO)
    implementation(Deps.Ktor.clientContentNegotiation)
    implementation(Deps.clikt)
    implementation(Deps.logback)
    implementation(Deps.Moshi.adapters)
    implementation(Deps.Moshi.moshi)
    "kapt"(Deps.Moshi.codeGen)
}
