import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    kotlin("kapt")
    application
    id("com.github.johnrengelman.shadow")
}

application {
    mainClassName = "MainKt"
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

dependencies {
    implementation(project(":server"))
    implementation(project(":client"))
    implementation(kotlin("stdlib"))
    implementation(Deps.Ktor.netty)
    implementation(Deps.Ktor.clientCore)
    implementation(Deps.Ktor.clientCIO)
    implementation(Deps.Ktor.clientJson)
    implementation(Deps.Ktor.clientJsonJvm)
    implementation(Deps.clikt)
    implementation(Deps.logback)
    implementation(Deps.Moshi.adapters)
    implementation(Deps.Moshi.moshi)
    "kapt"(Deps.Moshi.codeGen)
}
