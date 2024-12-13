import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.kapt)
    application
    alias(libs.plugins.shadow)
}

application {
    mainClass.set("MainKt")
}

tasks.withType<KotlinCompile> {
    compilerOptions.jvmTarget.set(JvmTarget.JVM_1_8)
}

kotlin {
    jvmToolchain(8)
}

dependencies {
    implementation(project(":core"))
    implementation(libs.ktor.server.netty)
    implementation(libs.ktor.server.callLogging)
    implementation(libs.ktor.server.contentNegotiation)
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.contentNegotiation)
    implementation(libs.clikt)
    implementation(libs.logback)
    implementation(libs.moshi.adapters)
    implementation(libs.moshi)
    "kapt"(libs.moshi.codeGen)
}
