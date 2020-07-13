import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    kotlin("kapt")
    application
}

application {
    mainClassName = "MainKt"
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

dependencies {
    implementation(project(":server"))
    implementation(kotlin("stdlib"))
    implementation(Deps.Ktor.netty)
    implementation(Deps.logback)
    implementation(Deps.Moshi.adapters)
    implementation(Deps.Moshi.moshi)
    "kapt"(Deps.Moshi.codeGen)
}
