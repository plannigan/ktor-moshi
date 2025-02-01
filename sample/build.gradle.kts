import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ksp)
    application
    alias(libs.plugins.shadow)
}

application {
    mainClass.set("MainKt")
}

tasks.withType<KotlinCompile> {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_17
    }
}

kotlin {
    jvmToolchain(17)
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
    ksp(libs.moshi.codeGen)
}
